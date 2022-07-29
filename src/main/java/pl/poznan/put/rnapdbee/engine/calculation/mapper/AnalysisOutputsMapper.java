package pl.poznan.put.rnapdbee.engine.calculation.mapper;

import edu.put.rnapdbee.analysis.AnalysisOutput;
import edu.put.rnapdbee.analysis.elements.StructuralElement;
import edu.put.rnapdbee.analysis.elements.StructuralElementFinder;
import edu.put.rnapdbee.visualization.SecondaryStructureImage;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.engine.calculation.model.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.calculation.model.Output2D;
import pl.poznan.put.rnapdbee.engine.calculation.model.SingleSecondaryModelAnalysisOutput;
import pl.poznan.put.rnapdbee.engine.calculation.model.SingleStrandOutput;
import pl.poznan.put.rnapdbee.engine.calculation.model.StructuralElementOutput;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Ct;
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
    public Output2D mapToImageAnalysisOutput(List<AnalysisOutput> analysisOutputs) {
        var singleAnalysisOutputs = analysisOutputs.stream().map(this::mapSingleAnalysisOutputToImage).collect(Collectors.toList());
        return new Output2D()
                .withAnalysis(singleAnalysisOutputs);
    }

    /**
     * Maps {@link AnalysisOutput} into {@link SingleSecondaryModelAnalysisOutput}
     *
     * @param analysisOutput analysis output which is mapped to SingleSecondaryModelAnalysisOutput object
     */
    private SingleSecondaryModelAnalysisOutput mapSingleAnalysisOutputToImage(AnalysisOutput analysisOutput) {
        return new SingleSecondaryModelAnalysisOutput()
                .withBpSeq(analysisOutput
                        .bpSeq().entries().stream()
                        .map(BpSeq.Entry::toString)
                        .collect(Collectors.toList()))
                .withStrands(analysisOutput
                        .dotBracket().strands().stream()
                        .map(this::mapStrandIntoSingleStrandOutput)
                        .collect(Collectors.toList()))
                .withCt(analysisOutput
                        .ct().entries().stream()
                        .map(Ct.ExtendedEntry::toString)
                        .collect(Collectors.toList()))
                .withInteractions(analysisOutput
                        .getInterStrand().stream()
                        .map(interStrand -> interStrand.basePair().toString())
                        .collect(Collectors.toList()))
                .withStructuralElement(
                        mapStructuralElementFinderIntoStructuralElementOutput(analysisOutput.structuralElementFinder()))
                .withImageInformation(mapSecondaryStructureImageIntoImageInformationOutput(analysisOutput.image()));
    }

    private SingleStrandOutput mapStrandIntoSingleStrandOutput(Strand strand) {
        return new SingleStrandOutput()
                .withName(strand.name())
                .withSequence(strand.sequence())
                .withStructure(strand.structure());
    }

    private StructuralElementOutput mapStructuralElementFinderIntoStructuralElementOutput(
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

    private ImageInformationOutput mapSecondaryStructureImageIntoImageInformationOutput(SecondaryStructureImage image) {
        return new ImageInformationOutput()
                .withSuccessfulDrawer(image.getSuccessfulDrawer())
                .withFailedDrawer(image.getFailedDrawer())
                .withPathToPNGImage(image.getPngUrl())
                .withPathToSVGImage(image.getSvgUrl());
    }
}
