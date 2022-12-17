package pl.poznan.put.rnapdbee.engine.calculation.secondary.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.structure.formats.Strand;

/**
 * DTO class representing single Strand output
 */
public class SingleStrandOutput {

    @JsonProperty("name")
    private final String fullName;
    @JsonProperty("sequence")
    private final String sequence;
    @JsonProperty("structure")
    private final String structure;

    @JsonIgnore
    private final String shortName;

    public static SingleStrandOutput ofStrand(Strand strand) {
        return new SingleStrandOutputBuilder()
                .withName(strand.name())
                .withSequence(strand.sequence())
                .withStructure(strand.structure())
                .build();
    }

    private SingleStrandOutput(String name, String sequence, String structure) {
        this.fullName = ">strand_" + (name == null ? "" : name);
        this.sequence = sequence;
        this.structure = structure;
        this.shortName = name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSequence() {
        return sequence;
    }

    public String getStructure() {
        return structure;
    }

    public String getShortName() {
        return shortName;
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
