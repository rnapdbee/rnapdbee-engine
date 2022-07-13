package pl.poznan.put.rnapdbee.engine.calculation.model;

/**
 * DTO class representing single Strand output
 */
public class SingleStrandOutput {

    private final String name;
    private final String sequence;
    private final String structure;

    public String getName() {
        return name;
    }

    public String getSequence() {
        return sequence;
    }

    public String getStructure() {
        return structure;
    }

    public SingleStrandOutput(String name, String sequence, String structure) {
        this.name = name;
        this.sequence = sequence;
        this.structure = structure;
    }
}
