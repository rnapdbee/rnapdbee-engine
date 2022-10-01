package pl.poznan.put.rnapdbee.engine.calculation.logic;


import edu.put.rnapdbee.analysis.AnalysisOutput;
import edu.put.rnapdbee.analysis.AnalysisResult;
import edu.put.rnapdbee.analysis.ImmutableAnalysisOutput;
import edu.put.rnapdbee.analysis.InputStructureName;
import edu.put.rnapdbee.analysis.elements.StructuralElementFinder;
import edu.put.rnapdbee.analysis.proxies.AnalysisResultBuilder;
import edu.put.rnapdbee.enums.ConverterEnum;
import edu.put.rnapdbee.visualization.SecondaryStructureImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.rnapdbee.engine.calculation.model.SecondaryFileExtensionEnum;
import pl.poznan.put.rnapdbee.engine.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.model.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.BasePair;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.ImmutableAnalyzedBasePair;
import pl.poznan.put.structure.ImmutableBasePair;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DefaultDotBracket;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.Strand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class handling (...) -> Image calculation
 */
@Component
public class SecondaryStructureAnalysisService {

    ImageService imageService;

    // TODO: replace converter method with Mixed-Integer Linear Programming (separate Task)
    final Converter CONVERTER = ConverterEnum.DPNEW;

    /**
     * performs analysis of Secondary RNA structures to Images
     *
     * @param structuralElementsHandling enum determining if pseudoknots should be considered or not
     * @param visualizationTool          enum for Visualization Tool
     * @param content                    content of the uploaded file
     * @param removeIsolated             indicates if isolated pairs should be taken into account in analysis
     * @param filename                   name of loaded file structure
     * @return List of {@link AnalysisOutput}
     */
    public List<AnalysisOutput> analyseSecondaryStructureFile(StructuralElementsHandling structuralElementsHandling,
                                                              VisualizationTool visualizationTool,
                                                              boolean removeIsolated,
                                                              String content,
                                                              String filename) {
        DotBracket dotBracket = convertSecondaryFileIntoDbnFormat(filename.split("\\.")[1], removeIsolated, content);
        return analyseDotBracket(dotBracket, filename, structuralElementsHandling, visualizationTool);
    }

    private List<AnalysisOutput> analyseDotBracket(DotBracket dotBracket,
                                                   String filename,
                                                   StructuralElementsHandling structuralElementsHandling,
                                                   VisualizationTool visualizationTool) {
        // Assuming the filename is full name of file and prefix is the name without file extension
        InputStructureName source = new InputStructureName(filename, filename.split("\\.")[0]);

        return dotBracket.combineStrands().stream()
                .map(combinedStrand ->
                        analyseSingleCombinedStrand(combinedStrand, structuralElementsHandling, visualizationTool, source))
                .collect(Collectors.toList());
    }

    private AnalysisOutput analyseSingleCombinedStrand(DotBracket combinedStrand,
                                                       StructuralElementsHandling structuralElementsHandling,
                                                       VisualizationTool visualizationTool,
                                                       InputStructureName source) {

        final BpSeq bpSeqFromCombined = BpSeq.fromDotBracket(combinedStrand);
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
                // setting 1 based on rnapdbee-web implementation TODO: analyze how to do it better
                .modelNumber(1)
                .source(source)
                .bpSeq(bpSeqFromCombined)
                .ct(ctFromCombined)
                .dotBracket(combinedStrand)
                .image(image)
                // setting empty list based on rnapdbee-web implementation TODO: analyze how to do it better
                .messages(Collections.emptyList())
                .structuralElementFinder(structuralElementFinder)
                .analysisResult(analysisResult)
                // setting empty String based on rnapdbee-web implementation TODO: analyze how to do it better
                .title("")
                .build();
    }

    private List<AnalyzedBasePair> analyzeInterStrandPairs(final DotBracket combinedStrand) {
        final List<AnalyzedBasePair> interStrand = new ArrayList<>();
        final Map<DotBracketSymbol, DotBracketSymbol> pairs = combinedStrand.pairs();

        combinedStrand.symbols().forEach(symbolMine -> {
            if (!pairs.containsKey(symbolMine)) {
                return;
            }

            final DotBracketSymbol symbolPair = pairs.get(symbolMine);
            if (symbolMine.index() >= symbolPair.index()) {
                return;
            }

            final Strand strandMine = combinedStrand.findStrand(symbolMine);
            final Strand strandPair = combinedStrand.findStrand(symbolPair);
            if (strandMine.equals(strandPair)) {
                return;
            }

            final PdbNamedResidueIdentifier left =
                    ImmutablePdbNamedResidueIdentifier.of(
                            strandMine.name().replaceFirst("strand_", ""),
                            symbolMine.index() + 1,
                            Optional.empty(),
                            symbolMine.sequence());
            final PdbNamedResidueIdentifier right =
                    ImmutablePdbNamedResidueIdentifier.of(
                            strandPair.name().replaceFirst("strand_", ""),
                            symbolPair.index() + 1,
                            Optional.empty(),
                            symbolPair.sequence());
            final BasePair basePair = ImmutableBasePair.of(left, right);
            interStrand.add(ImmutableAnalyzedBasePair.of(basePair));
        });
        return interStrand;
    }

    private DotBracket convertSecondaryFileIntoDbnFormat(String fileExtension, boolean removeIsolated, String content) {
        if (fileExtension.equals(SecondaryFileExtensionEnum.BP_SEQ.fileExtension)) {
            return convertBpSeqIntoDotBracket(content, removeIsolated);
        } else if (fileExtension.equals(SecondaryFileExtensionEnum.CT.fileExtension)) {
            return convertCtIntoDotBracket(content, removeIsolated);
        } else if (fileExtension.equals(SecondaryFileExtensionEnum.DBN.fileExtension)) {
            return readDotBracketContent(content, removeIsolated);
        } else {
            throw new IllegalArgumentException(
                    "Invalid attempt to analyze secondary structure for input type: " + fileExtension);
        }
    }

    private DotBracket convertBpSeqIntoDotBracket(String content, boolean removeIsolated) {
        BpSeq bpSeq = removeIsolated
                ? BpSeq.fromString(content).withoutIsolatedPairs()
                : BpSeq.fromString(content);
        Ct ct = Ct.fromBpSeq(bpSeq);
        return DefaultDotBracket.copyWithStrands(CONVERTER.convert(bpSeq), ct);
    }

    private DotBracket convertCtIntoDotBracket(String content, boolean removeIsolated) {
        Ct ct = removeIsolated
                ? Ct.fromString(content).withoutIsolatedPairs()
                : Ct.fromString(content);
        BpSeq bpSeq = BpSeq.fromCt(ct);
        return DefaultDotBracket.copyWithStrands(CONVERTER.convert(bpSeq), ct);
    }

    private DotBracket readDotBracketContent(String content, boolean removeIsolated) {
        DotBracket readDotBracket = DefaultDotBracket.fromString(content);
        if (removeIsolated) {
            return DefaultDotBracket.copyWithoutIsolatedBasePairs(readDotBracket);
        } else {
            return readDotBracket;
        }
    }

    @Autowired
    public SecondaryStructureAnalysisService(ImageService imageService) {
        this.imageService = imageService;
    }
}
