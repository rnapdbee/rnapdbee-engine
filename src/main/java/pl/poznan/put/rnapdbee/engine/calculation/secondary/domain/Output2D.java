package pl.poznan.put.rnapdbee.engine.calculation.secondary.domain;


import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.ImageInformationOutput;

import java.util.List;

/**
 * DTO class representing response body secondary structure analysis.
 * Relevant for "(...) -> image" and "2D -> (...)" scenarios.
 */
public class Output2D {

    private List<SingleStrandOutput> strands;
    private List<String> bpSeq;
    private List<String> ct;
    private List<String> interactions;
    private StructuralElementOutput structuralElements;
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
        return structuralElements;
    }

    public ImageInformationOutput getImageInformation() {
        return imageInformation;
    }

    public Output2D withStrands(List<SingleStrandOutput> strands) {
        this.strands = strands;
        return this;
    }

    public Output2D withBpSeq(List<String> bpSeq) {
        this.bpSeq = bpSeq;
        return this;
    }

    public Output2D withCt(List<String> ct) {
        this.ct = ct;
        return this;
    }

    public Output2D withInteractions(List<String> interactions) {
        this.interactions = interactions;
        return this;
    }

    public Output2D withStructuralElement(StructuralElementOutput structuralElements) {
        this.structuralElements = structuralElements;
        return this;
    }

    public Output2D withImageInformation(ImageInformationOutput imageInformation) {
        this.imageInformation = imageInformation;
        return this;
    }
}
