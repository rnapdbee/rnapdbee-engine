package com.example.rnapdbeeEngine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for ResidueAuth
 */
public class ResidueAuth {

    @JsonProperty("chain")
    private String chain;

    @JsonProperty("number")
    private Integer number;

    @JsonProperty("icode")
    private String icode;

    @JsonProperty("name")
    private String name;

    public String getChain() {
        return chain;
    }

    public Integer getNumber() {
        return number;
    }

    public String getIcode() {
        return icode;
    }

    public String getName() {
        return name;
    }

}
