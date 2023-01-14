package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

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
        this.insertionCode = residueAuth.getInsertionCode().orElse(null);
        this.name = residueAuth.getName();
    }

    public static ResidueAuth ofResidueAuthWithExchangedName(ResidueAuth residueAuth,
                                                             String name) {
        ResidueAuth newResidueAuth = new ResidueAuth(residueAuth);
        if (!name.equals(residueAuth.name)) {
            newResidueAuth.name = name.toLowerCase();
        }
        return newResidueAuth;
    }

    public String getChainIdentifier() {
        return chainIdentifier;
    }

    public Integer getResidueNumber() {
        return residueNumber;
    }

    public Optional<String> getInsertionCode() {
        return Optional.ofNullable(insertionCode);
    }

    public String getName() {
        return name;
    }
}
