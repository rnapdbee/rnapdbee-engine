package pl.poznan.put.rnapdbee.engine.shared.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.engine.shared.elements.StructuralElement;
import pl.poznan.put.rnapdbee.engine.shared.elements.StructuralElementFinder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO class representing single Structural Element output
 */
public class StructuralElementOutput {

    @JsonProperty("stems")
    private final List<String> stems;
    @JsonProperty("loops")
    private final List<String> loops;
    @JsonProperty("singleStrands")
    private final List<String> singleStrands;
    @JsonProperty("singleStrands5p")
    private final List<String> singleStrands5p;
    @JsonProperty("singleStrands3p")
    private final List<String> singleStrands3p;
    @JsonProperty("coordinates")
    private final String coordinates;

    public List<String> getStems() {
        return stems;
    }

    public List<String> getLoops() {
        return loops;
    }

    public List<String> getSingleStrands() {
        return singleStrands;
    }

    public List<String> getSingleStrands5p() {
        return singleStrands5p;
    }

    public List<String> getSingleStrands3p() {
        return singleStrands3p;
    }

    public String getCoordinates() {
        return coordinates;
    }

    private StructuralElementOutput(List<String> stems,
                                    List<String> loops,
                                    List<String> singleStrands,
                                    List<String> singleStrands5p,
                                    List<String> singleStrands3p,
                                    String coordinates) {
        this.stems = stems;
        this.loops = loops;
        this.singleStrands = singleStrands;
        this.singleStrands5p = singleStrands5p;
        this.singleStrands3p = singleStrands3p;
        this.coordinates = coordinates;
    }

    public static StructuralElementOutput EMPTY_INSTANCE = new StructuralElementOutput(
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null
    );

    public static StructuralElementOutput ofStructuralElementsFinder(StructuralElementFinder structuralElementFinder) {
        return new Builder()
                .withStems(structuralElementFinder.getStems().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()))
                .withLoops(structuralElementFinder.getLoops().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()))
                .withSingleStrands(structuralElementFinder.getSingleStrands().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()))
                .withSingleStrands5p(structuralElementFinder.getSingleStrands5p().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()))
                .withSingleStrands3p(structuralElementFinder.getSingleStrands3p().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()))
                .build();
    }

    public static StructuralElementOutput ofStructuralElementsFinderAndCoordinates(
            StructuralElementFinder structuralElementFinder,
            String coordinates) {
        return new Builder().withStems(structuralElementFinder.getStems().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()))
                .withLoops(structuralElementFinder.getLoops().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()))
                .withSingleStrands(structuralElementFinder.getSingleStrands().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()))
                .withSingleStrands5p(structuralElementFinder.getSingleStrands5p().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()))
                .withSingleStrands3p(structuralElementFinder.getSingleStrands3p().stream()
                        .map(StructuralElement::toString).collect(Collectors.toList()))
                .withCoordinates(coordinates).build();
    }


    public static class Builder {
        private List<String> stems;
        private List<String> loops;
        private List<String> singleStrands;
        private List<String> singleStrands5p;
        private List<String> singleStrands3p;
        private String coordinates;

        public Builder withStems(List<String> stems) {
            this.stems = stems;
            return this;
        }

        public Builder withLoops(List<String> loops) {
            this.loops = loops;
            return this;
        }

        public Builder withSingleStrands(List<String> singleStrands) {
            this.singleStrands = singleStrands;
            return this;
        }

        public Builder withSingleStrands5p(List<String> singleStrands5p) {
            this.singleStrands5p = singleStrands5p;
            return this;
        }

        public Builder withSingleStrands3p(List<String> singleStrands3p) {
            this.singleStrands3p = singleStrands3p;
            return this;
        }

        public Builder withCoordinates(String coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public StructuralElementOutput build() {
            return new StructuralElementOutput(stems, loops, singleStrands, singleStrands5p, singleStrands3p, coordinates);
        }
    }
}
