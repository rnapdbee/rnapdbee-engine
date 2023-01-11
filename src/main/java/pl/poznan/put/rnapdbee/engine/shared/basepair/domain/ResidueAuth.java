package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;


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

    public ResidueAuth() {
    }

    private ResidueAuth(ResidueAuth residueAuth) {
        this.chainIdentifier = residueAuth.getChainIdentifier();
        this.residueNumber = residueAuth.getResidueNumber();
        this.insertionCode = residueAuth.getInsertionCode();
        this.name = residueAuth.getName();
    }

    public static ResidueAuth ofResidueAuthWithExchangedName(ResidueAuth residueAuth,
                                                             String name) {
        ResidueAuth newResidueAuth = new ResidueAuth(residueAuth);
        newResidueAuth.name = name;
        return newResidueAuth;
    }

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
