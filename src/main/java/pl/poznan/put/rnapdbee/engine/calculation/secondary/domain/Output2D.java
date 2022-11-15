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

    private final List<SingleStrandOutput> strands;
    private final List<String> bpSeq;
    private final List<String> ct;
    private final List<String> interactions;
    private final StructuralElementOutput structuralElements;
    private final ImageInformationOutput imageInformation;

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

    private Output2D(List<SingleStrandOutput> strands,
                     List<String> bpSeq,
                     List<String> ct,
                     List<String> interactions,
                     StructuralElementOutput structuralElements,
                     ImageInformationOutput imageInformation) {
        this.strands = strands;
        this.bpSeq = bpSeq;
        this.ct = ct;
        this.interactions = interactions;
        this.structuralElements = structuralElements;
        this.imageInformation = imageInformation;
    }

    public static class Output2DBuilder {

        private List<SingleStrandOutput> strands;
        private List<String> bpSeq;
        private List<String> ct;
        private List<String> interactions;
        private StructuralElementOutput structuralElements;
        private ImageInformationOutput imageInformation;

        public Output2DBuilder withStrands(List<SingleStrandOutput> strands) {
            this.strands = strands;
            return this;
        }

        public Output2DBuilder withStrandsFromDotBracket(DotBracket dotBracket) {
            return withStrands(dotBracket.strands().stream()
                    .map(SingleStrandOutput::ofStrand)
                    .collect(Collectors.toList()));
        }

        public Output2DBuilder withBpSeq(List<String> bpSeq) {
            this.bpSeq = bpSeq;
            return this;
        }

        public Output2DBuilder withBpSeqFromBpSeqObject(BpSeq bpSeq) {
            return withBpSeq(bpSeq.entries().stream()
                    .map(BpSeq.Entry::toString)
                    .collect(Collectors.toList()));
        }

        public Output2DBuilder withCt(List<String> ct) {
            this.ct = ct;
            return this;
        }

        public Output2DBuilder withCtFromCt(Ct ct) {
            return withCt(ct.entries().stream()
                    .map(Ct.ExtendedEntry::toString)
                    .collect(Collectors.toList()));
        }

        public Output2DBuilder withInteractions(List<String> interactions) {
            this.interactions = interactions;
            return this;
        }

        public Output2DBuilder withStructuralElement(StructuralElementOutput structuralElements) {
            this.structuralElements = structuralElements;
            return this;
        }

        public Output2DBuilder withImageInformation(ImageInformationOutput imageInformation) {
            this.imageInformation = imageInformation;
            return this;
        }

        public Output2D build() {
            return new Output2D(strands, bpSeq, ct, interactions, structuralElements, imageInformation);
        }
    }
}
