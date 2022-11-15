package pl.poznan.put.rnapdbee.engine.calculation.secondary.domain;

import pl.poznan.put.structure.formats.Strand;

/**
 * DTO class representing single Strand output
 */
public class SingleStrandOutput {

    private final String name;
    private final String sequence;
    private final String structure;

    public static SingleStrandOutput ofStrand(Strand strand) {
        return new SingleStrandOutputBuilder()
                .withName(strand.name())
                .withSequence(strand.sequence())
                .withStructure(strand.structure())
                .build();
    }

    private SingleStrandOutput(String name, String sequence, String structure) {
        this.name = name;
        this.sequence = sequence;
        this.structure = structure;
    }

    public String getName() {
        return name;
    }

    public String getSequence() {
        return sequence;
    }

    public String getStructure() {
        return structure;
    }

    public static class SingleStrandOutputBuilder {
        private String name;
        private String sequence;
        private String structure;

        public SingleStrandOutputBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SingleStrandOutputBuilder withSequence(String sequence) {
            this.sequence = sequence;
            return this;
        }

        public SingleStrandOutputBuilder withStructure(String structure) {
            this.structure = structure;
            return this;
        }

        public SingleStrandOutput build() {
            return new SingleStrandOutput(name, sequence, structure);
        }
    }
}
