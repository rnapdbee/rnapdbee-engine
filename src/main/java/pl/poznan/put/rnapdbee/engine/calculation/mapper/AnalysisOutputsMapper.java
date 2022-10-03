package pl.poznan.put.rnapdbee.engine.calculation.mapper;

import edu.put.rnapdbee.analysis.AnalysisOutput;
import edu.put.rnapdbee.analysis.elements.StructuralElement;
import edu.put.rnapdbee.analysis.elements.StructuralElementFinder;
import edu.put.rnapdbee.visualization.SecondaryStructureImage;
import org.springframework.stereotype.Service;
import pl.poznan.put.consensus.BpSeqInfo;
import pl.poznan.put.rnapdbee.engine.calculation.model.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.calculation.model.Output2D;
import pl.poznan.put.rnapdbee.engine.calculation.model.SingleSecondaryModelAnalysisOutput;
import pl.poznan.put.rnapdbee.engine.calculation.model.SingleStrandOutput;
import pl.poznan.put.rnapdbee.engine.calculation.model.StructuralElementOutput;
import pl.poznan.put.rnapdbee.engine.model.OutputMultiEntry;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.Strand;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalysisOutputsMapper {

    /**
     * Maps List of {@link AnalysisOutput} to {@link Output2D} object
     *
     * @param analysisOutputs - output of analysis
     * @return {@link Output2D} result object
     */
    public Output2D mapToOutput2D(List<AnalysisOutput> analysisOutputs) {
        var singleAnalysisOutputs = analysisOutputs.stream()
                .map(this::mapSingleAnalysisOutputToSecondaryModelAnalysisOutput).collect(Collectors.toList());
        return new Output2D()
                .withAnalysis(singleAnalysisOutputs);
    }

    /**
     * maps bpSeqInfo object and secondaryStructureImage object into OutputMultiEntry.
     *
     * @param bpSeqInfo              bpSeqInfo object
     * @param secondaryVisualization visualization of the analysed bpSeq
     * @return important information wrapped in OutputMultiEntry object
     */
    public OutputMultiEntry mapBpSeqInfoAndSecondaryStructureImageIntoOutputMultiEntry(BpSeqInfo bpSeqInfo,
                                                                                       SecondaryStructureImage secondaryVisualization) {
        SingleSecondaryModelAnalysisOutput secondaryAnalysisOutput = new SingleSecondaryModelAnalysisOutput()
                .withBpSeq(mapBpSeqToListOfString(bpSeqInfo.getBpSeq()))
                .withCt(mapCtToListOfString(bpSeqInfo.getCt()))
                // TODO remove this need to get(0) when merging rnapdbee-common code to engine.
                .withStrands(mapDotBracketIntoStrandOutputs(bpSeqInfo.getDotBracketInfos().get(0).getDotBracket()))
                .withImageInformation(mapSecondaryStructureImageIntoImageInformationOutput(secondaryVisualization));
        Output2D output2D = new Output2D()
                .withAnalysis(List.of(secondaryAnalysisOutput));

        return new OutputMultiEntry()
                .withOutput2D(output2D)
                .withAdapterEnums(bpSeqInfo.getBasePairAnalyzerNames());
    }

    // TODO make private
    public List<String> mapBpSeqToListOfString(BpSeq bpSeq) {
        return bpSeq.entries().stream()
                .map(BpSeq.Entry::toString)
                .collect(Collectors.toList());
    }

    // TODO make private
    public List<String> mapCtToListOfString(Ct ct) {
        return ct.entries().stream()
                .map(Ct.ExtendedEntry::toString)
                .collect(Collectors.toList());
    }

    // TODO make private
    public ImageInformationOutput mapSecondaryStructureImageIntoImageInformationOutput(SecondaryStructureImage image) {
        return new ImageInformationOutput()
                .withSuccessfulDrawer(image.getSuccessfulDrawer())
                .withFailedDrawer(image.getFailedDrawer())
                .withPathToPNGImage(image.getPngUrl())
                .withPathToSVGImage(image.getSvgUrl());
    }

    /**
     * Maps {@link AnalysisOutput} into {@link SingleSecondaryModelAnalysisOutput}
     *
     * @param analysisOutput analysis output which is mapped to SingleSecondaryModelAnalysisOutput object
     */
    private SingleSecondaryModelAnalysisOutput mapSingleAnalysisOutputToSecondaryModelAnalysisOutput(AnalysisOutput analysisOutput) {
        return new SingleSecondaryModelAnalysisOutput()
                .withBpSeq(mapBpSeqToListOfString(analysisOutput.bpSeq()))
                .withStrands(mapDotBracketIntoStrandOutputs(analysisOutput.dotBracket()))
                .withCt(mapCtToListOfString(analysisOutput.ct()))
                .withInteractions(analysisOutput
                        .getInterStrand().stream()
                        .map(interStrand -> interStrand.basePair().toString())
                        .collect(Collectors.toList()))
                .withStructuralElement(
                        mapStructuralElementFinderIntoStructuralElementOutput(analysisOutput.structuralElementFinder()))
                .withImageInformation(mapSecondaryStructureImageIntoImageInformationOutput(analysisOutput.image()));
    }

    private List<SingleStrandOutput> mapDotBracketIntoStrandOutputs(DotBracket dotBracket) {
        return dotBracket.strands().stream()
                .map(this::mapStrandIntoSingleStrandOutput)
                .collect(Collectors.toList());
    }

    private SingleStrandOutput mapStrandIntoSingleStrandOutput(Strand strand) {
        return new SingleStrandOutput()
                .withName(strand.name())
                .withSequence(strand.sequence())
                .withStructure(strand.structure());
    }

    // TODO make private
    public StructuralElementOutput mapStructuralElementFinderIntoStructuralElementOutput(
            StructuralElementFinder structuralElementFinder) {
        return new StructuralElementOutput()
                .withStems(structuralElementFinder.getStems().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()))
                .withLoops(structuralElementFinder.getLoops().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()))
                .withSingleStrands(structuralElementFinder.getSingleStrands().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()))
                .withSingleStrands5p(structuralElementFinder.getSingleStrands5p().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()))
                .withSingleStrands3p(structuralElementFinder.getSingleStrands3p().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()));
    }
}
