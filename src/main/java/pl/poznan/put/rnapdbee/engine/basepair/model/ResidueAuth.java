package pl.poznan.put.rnapdbee.engine.basepair.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for ResidueAuth
 */
public class ResidueAuth {

    @JsonProperty("chain")
    private String chainIdentifier;

    @JsonProperty("number")
    private Integer residueNumber;

    @JsonProperty("icode")
    private String insertionCode;

    @JsonProperty("name")
    private String name;

    public String getChainIdentifier() {
        return chainIdentifier;
    }

    public Integer getResidueNumber() {
        return residueNumber;
    }

    public String getInsertionCode() {
        return insertionCode;
    }

    public String getName() {
        return name;
    }
}
