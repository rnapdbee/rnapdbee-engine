package pl.poznan.put.rnapdbee.engine.shared.integration.adapters.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.SingleStrandOutput;
import pl.poznan.put.structure.formats.Strand;

public class StrandPayload {

    @JsonProperty("name")
    private final String name;
    @JsonProperty("sequence")
    private final String sequence;
    @JsonProperty("structure")
    private final String structure;

    public static StrandPayload ofStrand(Strand strand) {
        return new StrandPayload(strand.name(), strand.sequence(), strand.structure());
    }

    public static StrandPayload ofStrandOutput(SingleStrandOutput strand) {
        return new StrandPayload(strand.getShortName(), strand.getSequence(), strand.getStructure());
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

    private StrandPayload(String name, String sequence, String structure) {
        this.name = name;
        this.sequence = sequence;
        this.structure = structure;
    }
}
