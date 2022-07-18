package pl.poznan.put.rnapdbee.engine.calculation.model;

import java.util.List;

/**
 * DTO class representing structure of single (...) -> image output
 */
public class SingleDotBracketToImageAnalysisOutput {

    private List<SingleStrandOutput> strands;
    private List<String> bpseq;
    private List<String> ct;
    private List<String> interactions;
    private StructuralElementOutput structuralElement;
    private ImageInformationOutput imageInformation;

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
        return structuralElement;
    }

    public ImageInformationOutput getImageInformation() {
        return imageInformation;
    }

    public SingleDotBracketToImageAnalysisOutput withStrands(List<SingleStrandOutput> strands) {
        this.strands = strands;
        return this;
    }

    public SingleDotBracketToImageAnalysisOutput withBpseq(List<String> bpseq) {
        this.bpseq = bpseq;
        return this;
    }

    public SingleDotBracketToImageAnalysisOutput withCt(List<String> ct) {
        this.ct = ct;
        return this;
    }

    public SingleDotBracketToImageAnalysisOutput withInteractions(List<String> interactions) {
        this.interactions = interactions;
        return this;
    }

    public SingleDotBracketToImageAnalysisOutput withStructuralElement(StructuralElementOutput structuralElement) {
        this.structuralElement = structuralElement;
        return this;
    }

    public SingleDotBracketToImageAnalysisOutput withImageInformation(ImageInformationOutput imageInformation) {
        this.imageInformation = imageInformation;
        return this;
    }
}
