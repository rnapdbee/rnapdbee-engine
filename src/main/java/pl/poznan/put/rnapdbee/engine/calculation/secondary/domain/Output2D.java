package pl.poznan.put.rnapdbee.engine.calculation.secondary.domain;


import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.ImageInformationOutput;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DotBracket;

import java.util.List;
import java.util.stream.Collectors;

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

    public Output2D withStrandsFromDotBracket(DotBracket dotBracket) {
        return withStrands(dotBracket.strands().stream()
                .map(SingleStrandOutput::ofStrand)
                .collect(Collectors.toList()));
    }

    public Output2D withBpSeq(List<String> bpSeq) {
        this.bpSeq = bpSeq;
        return this;
    }

    public Output2D withBpSeqFromBpSeqObject(BpSeq bpSeq) {
        return withBpSeq(bpSeq.entries().stream()
                .map(BpSeq.Entry::toString)
                .collect(Collectors.toList()));
    }

    public Output2D withCt(List<String> ct) {
        this.ct = ct;
        return this;
    }

    public Output2D withCtFromCt(Ct ct) {
        return withCt(ct.entries().stream()
                .map(Ct.ExtendedEntry::toString)
                .collect(Collectors.toList()));
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
    // TODO refactor to builder pattern for consistency of Output2D, Output3D and OutputMulti
}
