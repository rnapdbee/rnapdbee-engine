package pl.poznan.put.rnapdbee.engine.basepair.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for Residue Label
 */
public class ResidueLabel {

    @JsonProperty("chain")
    private String chainIdentifier;

    @JsonProperty("number")
    private Integer residueNumber;

    @JsonProperty("name")
    private String name;

    public String getChainIdentifier() {
        return chainIdentifier;
    }

    public Integer getResidueNumber() {
        return residueNumber;
    }

    public String getName() {
        return name;
    }
}
