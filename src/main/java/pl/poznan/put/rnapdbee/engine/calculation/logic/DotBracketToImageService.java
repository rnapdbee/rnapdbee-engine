package pl.poznan.put.rnapdbee.engine.calculation.logic;


import edu.put.rnapdbee.analysis.AnalysisOutput;
import edu.put.rnapdbee.analysis.AnalysisResult;
import edu.put.rnapdbee.analysis.ImmutableAnalysisOutput;
import edu.put.rnapdbee.analysis.InputStructureName;
import edu.put.rnapdbee.analysis.elements.StructuralElementFinder;
import edu.put.rnapdbee.analysis.proxies.AnalysisResultBuilder;
import edu.put.rnapdbee.visualization.SecondaryStructureImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.rnapdbee.engine.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.model.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.BasePair;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.ImmutableAnalyzedBasePair;
import pl.poznan.put.structure.ImmutableBasePair;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DefaultDotBracket;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.Strand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class handling (...) -> Image calculation
 */
@Component
public class DotBracketToImageService {

    // TODO put in autowired constructor
    @Autowired
    ImageService imageService;

    /**
     * performs calculation of Dot Bracket RNA structures to Images (...) -> Image
     *
     * @param structuralElementsHandling enum determining if pseudoknots should be considered or not
     * @param visualizationTool          enum for Visualization Tool
     * @param content                    content of the uploaded file
     * @return List of {@link AnalysisOutput}
     */
    public List<AnalysisOutput> performDotBracketToImageCalculation(StructuralElementsHandling structuralElementsHandling,
                                                                    VisualizationTool visualizationTool,
                                                                    String content,
                                                                    String fileName) {
        var dotBracket = DefaultDotBracket.fromString(content);
        // Assuming the fileName is full name of file and prefix is the name without file extension
        InputStructureName source = new InputStructureName(fileName, fileName.split("\\.")[0]);

        return dotBracket.combineStrands().stream()
                .map(combinedStrand ->
                        analyseSingleCombinedStrand(combinedStrand, structuralElementsHandling, visualizationTool, source))
                .collect(Collectors.toList());
    }

    private AnalysisOutput analyseSingleCombinedStrand(DotBracket combinedStrand,
                                                       StructuralElementsHandling structuralElementsHandling,
                                                       VisualizationTool visualizationTool,
                                                       InputStructureName source) {

        final BpSeq bpseqFromCombined = BpSeq.fromDotBracket(combinedStrand);
        final Ct ctFromCombined = Ct.fromDotBracket(combinedStrand);

        SecondaryStructureImage image = imageService.provideVisualization(visualizationTool, combinedStrand);

        final StructuralElementFinder structuralElementFinder =
                new StructuralElementFinder(
                        combinedStrand,
                        structuralElementsHandling.canElementsEndWithPseudoknots(),
                        structuralElementsHandling.isReuseSingleStrandsFromLoopsEnabled());

        final List<AnalyzedBasePair> interStrand = analyzeInterStrandPairs(combinedStrand);
        final AnalysisResult analysisResult =
                AnalysisResultBuilder.builder().interStrand(interStrand).build();

        return ImmutableAnalysisOutput.builder()
                // setting 1 based on rnapdbee-web implementation
                .modelNumber(1)
                .source(source)
                .bpSeq(bpseqFromCombined)
                .ct(ctFromCombined)
                .dotBracket(combinedStrand)
                .image(image)
                // setting empty list based on rnapdbee-web implementation
                .messages(Collections.emptyList())
                .structuralElementFinder(structuralElementFinder)
                .analysisResult(analysisResult)
                // setting empty String based on rnapdbee-web implementation
                .title("")
                .build();
    }

    // TODO put in a service class - AnalysisService? -> analyze if needed when there are more similar methods
    private List<AnalyzedBasePair> analyzeInterStrandPairs(final DotBracket combinedStrand) {
        final List<AnalyzedBasePair> interStrand = new ArrayList<>();
        final Map<DotBracketSymbol, DotBracketSymbol> pairs = combinedStrand.pairs();

        for (final DotBracketSymbol symbolMine : combinedStrand.symbols()) {
            if (pairs.containsKey(symbolMine)) {
                final DotBracketSymbol symbolPair = pairs.get(symbolMine);

                if (symbolMine.index() < symbolPair.index()) {
                    final Strand strandMine = combinedStrand.findStrand(symbolMine);
                    final Strand strandPair = combinedStrand.findStrand(symbolPair);

                    if (!strandMine.equals(strandPair)) {
                        final PdbNamedResidueIdentifier left =
                                ImmutablePdbNamedResidueIdentifier.of(
                                        strandMine.name().replaceFirst("strand_", ""),
                                        symbolMine.index() + 1,
                                        "",
                                        symbolMine.sequence());
                        final PdbNamedResidueIdentifier right =
                                ImmutablePdbNamedResidueIdentifier.of(
                                        strandPair.name().replaceFirst("strand_", ""),
                                        symbolPair.index() + 1,
                                        "",
                                        symbolPair.sequence());
                        final BasePair basePair = ImmutableBasePair.of(left, right);
                        interStrand.add(ImmutableAnalyzedBasePair.of(basePair));
                    }
                }
            }
        }
        return interStrand;
    }

}
