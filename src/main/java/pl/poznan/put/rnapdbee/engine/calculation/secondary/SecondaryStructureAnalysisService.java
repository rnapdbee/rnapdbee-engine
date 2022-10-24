package pl.poznan.put.rnapdbee.engine.calculation.secondary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.Output2D;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.SecondaryFileExtensionEnum;
import pl.poznan.put.rnapdbee.engine.shared.elements.StructuralElementFinder;
import pl.poznan.put.rnapdbee.engine.shared.converter.KnotRemoval;
import pl.poznan.put.rnapdbee.engine.shared.converter.RNAStructure;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.BasePair;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.ImmutableAnalyzedBasePair;
import pl.poznan.put.structure.ImmutableBasePair;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DefaultDotBracket;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.ImmutableDefaultDotBracket;
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

    ImageService imageService;

    // TODO: replace converter method with Mixed-Integer Linear Programming (separate Task)
    //final Converter CONVERTER = ConverterEnum.DPNEW;

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
        DotBracket dotBracket = convertSecondaryFileIntoDbnFormat(filename.split("\\.")[1], removeIsolated, content);
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

        return new Output2D()
                .withStructuralElement(StructuralElementOutput.ofStructuralElementsFinder(structuralElementFinder))
                .withInteractions(interStrands.stream()
                        .map(interStrand -> interStrand.basePair().toString())
                        .collect(Collectors.toList()))
                .withImageInformation(imageInformation)
                .withStrandsFromDotBracket(dotBracket)
                .withCtFromCt(ctFromCombined)
                .withBpSeqFromBpSeqObject(bpSeqFromCombined);
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
        return DefaultDotBracket.copyWithStrands(convert(bpSeq), ct);
    }

    private DotBracket convertCtIntoDotBracket(String content, boolean removeIsolated) {
        Ct ct = removeIsolated
                ? Ct.fromString(content).withoutIsolatedPairs()
                : Ct.fromString(content);
        BpSeq bpSeq = BpSeq.fromCt(ct);
        return DefaultDotBracket.copyWithStrands(convert(bpSeq), ct);
    }

    private DotBracket readDotBracketContent(String content, boolean removeIsolated) {
        DotBracket readDotBracket = DefaultDotBracket.fromString(content);
        if (removeIsolated) {
            return DefaultDotBracket.copyWithoutIsolatedBasePairs(readDotBracket);
        } else {
            return readDotBracket;
        }
    }

    // TODO: using copied DP_NEW implementation from rnapdbee-common right now, change to MILP and remove whole
    //  pl.poznan.put.rnapdbee.engine.shared.converter package!!!
    private DotBracket convert(BpSeq bpSeq) {
        RNAStructure structure = new RNAStructure(bpSeq);
        structure = KnotRemoval.dynamicProgrammingOneBest(structure);
        return ImmutableDefaultDotBracket.of(
                structure.getSequence(), structure.getDotBracketStructure());
    }

    @Autowired
    public SecondaryStructureAnalysisService(ImageService imageService) {
        this.imageService = imageService;
    }
}
