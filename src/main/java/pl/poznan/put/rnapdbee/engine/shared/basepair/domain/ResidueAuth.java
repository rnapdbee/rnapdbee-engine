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

    public String getChainIdentifier() {
        return chainIdentifier;
    }

    public void setChainIdentifier(String chainIdentifier) {
        this.chainIdentifier = chainIdentifier;
    }

    public Integer getResidueNumber() {
        return residueNumber;
    }

    public void setResidueNumber(Integer residueNumber) {
        this.residueNumber = residueNumber;
    }

    public Optional<String> getInsertionCode() {
        if (insertionCode == null || "null".equals(insertionCode)) {
            return Optional.empty();
        }
        return Optional.of(insertionCode);
    }

    public void setInsertionCode(String insertionCode) {
        if ("null".equals(insertionCode)) {
            this.insertionCode = null;
        }  else {
            this.insertionCode = insertionCode;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ResidueAuth ofResidueAuthWithExchangedName(ResidueAuth residueAuth,
                                                             String name) {
        ResidueAuth newResidueAuth = new ResidueAuth(residueAuth);
        if (!name.equals(residueAuth.name)) {
            newResidueAuth.name = name.toLowerCase();
        }
        return newResidueAuth;
    }

    @Override
    public String toString() {
        return "ResidueAuth{" +
                "chainIdentifier='" + chainIdentifier + '\'' +
                ", residueNumber=" + residueNumber +
                ", insertionCode='" + insertionCode + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
