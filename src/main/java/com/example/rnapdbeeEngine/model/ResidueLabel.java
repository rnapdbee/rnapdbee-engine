package com.example.rnapdbeeEngine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for Residue Label
 */
public class ResidueLabel {
    @JsonProperty("chain")
    private String chain;

    @JsonProperty("number")
    private Integer number;

    @JsonProperty("name")
    private String name;

    public String getChain() {
        return chain;
    }

    public Integer getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

}
