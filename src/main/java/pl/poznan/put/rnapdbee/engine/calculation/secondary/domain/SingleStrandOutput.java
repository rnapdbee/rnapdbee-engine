package pl.poznan.put.rnapdbee.engine.calculation.secondary.domain;

import pl.poznan.put.structure.formats.Strand;

/**
 * DTO class representing single Strand output
 */
public class SingleStrandOutput {

    private String name;
    private String sequence;
    private String structure;

    public static SingleStrandOutput ofStrand(Strand strand) {
        return new SingleStrandOutput()
                .withName(strand.name())
                .withSequence(strand.sequence())
                .withStructure(strand.structure());
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

    public SingleStrandOutput withName(String name) {
        this.name = name;
        return this;
    }

    public SingleStrandOutput withSequence(String sequence) {
        this.sequence = sequence;
        return this;
    }

    public SingleStrandOutput withStructure(String structure) {
        this.structure = structure;
        return this;
    }
}
