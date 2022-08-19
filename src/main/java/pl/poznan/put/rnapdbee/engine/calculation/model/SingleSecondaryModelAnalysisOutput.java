package pl.poznan.put.rnapdbee.engine.calculation.model;

import java.util.List;

/**
 * DTO class representing structure of single secondary analysis output.
 * Relevant for "(...) -> image" and "2D -> (...)" scenarios.
 */
public class SingleSecondaryModelAnalysisOutput {

    private List<SingleStrandOutput> strands;
    private List<String> bpSeq;
    private List<String> ct;
    private List<String> interactions;
    private StructuralElementOutput structuralElement;
    private ImageInformationOutput imageInformation;

    public List<SingleStrandOutput> getStrands() {
        return strands;
    }

    public List<String> getBpSeq() {
        return bpSeq;
    }

    public List<String> getCt() {
        return ct;
    }

    public List<String> getInteractions() {
        return interactions;
    }

    public StructuralElementOutput getStructuralElements() {
        return structuralElement;
    }

    public ImageInformationOutput getImageInformation() {
        return imageInformation;
    }

    public SingleSecondaryModelAnalysisOutput withStrands(List<SingleStrandOutput> strands) {
        this.strands = strands;
        return this;
    }

    public SingleSecondaryModelAnalysisOutput withBpSeq(List<String> bpSeq) {
        this.bpSeq = bpSeq;
        return this;
    }

    public SingleSecondaryModelAnalysisOutput withCt(List<String> ct) {
        this.ct = ct;
        return this;
    }

    public SingleSecondaryModelAnalysisOutput withInteractions(List<String> interactions) {
        this.interactions = interactions;
        return this;
    }

    public SingleSecondaryModelAnalysisOutput withStructuralElement(
            StructuralElementOutput structuralElement) {
        this.structuralElement = structuralElement;
        return this;
    }

    public SingleSecondaryModelAnalysisOutput withImageInformation(
            ImageInformationOutput imageInformation) {
        this.imageInformation = imageInformation;
        return this;
    }
}
