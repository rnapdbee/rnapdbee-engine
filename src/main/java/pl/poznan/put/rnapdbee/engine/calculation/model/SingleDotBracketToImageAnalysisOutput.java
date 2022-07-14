package pl.poznan.put.rnapdbee.engine.calculation.model;

import edu.put.rnapdbee.analysis.AnalysisOutput;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Ct;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO class representing structure of single (...) -> image output
 */
public class SingleDotBracketToImageAnalysisOutput {

    private final List<SingleStrandOutput> strands;
    private final List<String> bpseq;
    private final List<String> ct;
    private final List<String> interactions;
    private final StructuralElementOutput structuralElements;
    private final ImageInformationOutput imageInformation;

    /**
     * Maps {@link AnalysisOutput} into {@link SingleDotBracketToImageAnalysisOutput}
     *
     * @param analysisOutput analysis output which is mapped to SingleDotBracketToImageAnalysisOutput object
     */
    public SingleDotBracketToImageAnalysisOutput(AnalysisOutput analysisOutput) {
        strands = analysisOutput
                .dotBracket().strands().stream()
                .map(strand -> new SingleStrandOutput(
                        strand.name(),
                        strand.sequence(),
                        strand.structure()))
                .collect(Collectors.toList());
        bpseq = analysisOutput
                .bpSeq().entries().stream()
                .map(BpSeq.Entry::toString).collect(Collectors.toList());
        ct = analysisOutput
                .ct().entries().stream()
                .map(Ct.ExtendedEntry::toString).collect(Collectors.toList());
        interactions = analysisOutput
                .getInterStrand().stream()
                .map(interStrand -> interStrand.basePair().toString()).collect(Collectors.toList());
        structuralElements = new StructuralElementOutput(analysisOutput.structuralElementFinder());
        imageInformation = new ImageInformationOutput(analysisOutput.image());
    }

    public List<SingleStrandOutput> getStrands() {
        return strands;
    }

    public List<String> getBpseq() {
        return bpseq;
    }

    public List<String> getCt() {
        return ct;
    }

    public List<String> getInteractions() {
        return interactions;
    }

    public StructuralElementOutput getStructuralElements() {
        return structuralElements;
    }

    public ImageInformationOutput getImageInformation() {
        return imageInformation;
    }
}
