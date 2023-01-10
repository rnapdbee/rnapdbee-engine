package pl.poznan.put.rnapdbee.engine.calculation.secondary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.Output2D;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputType;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputTypeDeterminer;
import pl.poznan.put.rnapdbee.engine.shared.elements.StructuralElementFinder;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.parser.SecondaryFileParser;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.BasePair;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.ImmutableAnalyzedBasePair;
import pl.poznan.put.structure.ImmutableBasePair;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.Strand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class handling (...) -> Image calculation
 */
@Component
public class SecondaryStructureAnalysisService {

    private final ImageService imageService;
    private final SecondaryFileParser secondaryFileParser;
    private final InputTypeDeterminer inputTypeDeterminer;

    /**
     * performs analysis of Secondary RNA structures to Images
     *
     * @param structuralElementsHandling enum determining if pseudoknots should be considered or not
     * @param visualizationTool          enum for Visualization Tool
     * @param content                    content of the uploaded file
     * @param removeIsolated             indicates if isolated pairs should be taken into account in analysis
     * @param filename                   name of loaded file structure
     * @return instance of {@link Output2D} class
     */
    public Output2D analyzeSecondaryStructureFile(StructuralElementsHandling structuralElementsHandling,
                                                  VisualizationTool visualizationTool,
                                                  boolean removeIsolated,
                                                  String content,
                                                  String filename) {
        InputType inputType = inputTypeDeterminer.detectSecondaryInputTypeFromFileName(filename);
        DotBracket dotBracket = secondaryFileParser.parseSecondaryFile(content, inputType, removeIsolated);
        return analyzeDotBracket(dotBracket, structuralElementsHandling, visualizationTool);
    }

    private Output2D analyzeDotBracket(DotBracket dotBracket,
                                       StructuralElementsHandling structuralElementsHandling,
                                       VisualizationTool visualizationTool) {
        final BpSeq bpSeqFromCombined = BpSeq.fromDotBracket(dotBracket);
        final Ct ctFromCombined = Ct.fromDotBracket(dotBracket);

        ImageInformationOutput imageInformation = imageService.visualizeCanonical(visualizationTool, dotBracket);

        final StructuralElementFinder structuralElementFinder =
                new StructuralElementFinder(
                        dotBracket,
                        structuralElementsHandling.canElementsEndWithPseudoknots(),
                        structuralElementsHandling.isReuseSingleStrandsFromLoopsEnabled());

        final List<AnalyzedBasePair> interStrands = analyzeInterStrandPairs(dotBracket);

        return new Output2D.Output2DBuilder()
                .withStructuralElement(StructuralElementOutput.ofStructuralElementsFinder(structuralElementFinder))
                .withInteractions(interStrands.stream()
                        .map(interStrand -> interStrand.basePair().toString())
                        .collect(Collectors.toList()))
                .withImageInformation(imageInformation)
                .withStrandsFromDotBracket(dotBracket)
                .withCtFromCt(ctFromCombined)
                .withBpSeqFromBpSeqObject(bpSeqFromCombined)
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

    @Autowired
    public SecondaryStructureAnalysisService(ImageService imageService,
                                             Converter converter, SecondaryFileParser secondaryFileParser, InputTypeDeterminer inputTypeDeterminer) {
        this.imageService = imageService;
        this.secondaryFileParser = secondaryFileParser;
        this.inputTypeDeterminer = inputTypeDeterminer;
    }
}
