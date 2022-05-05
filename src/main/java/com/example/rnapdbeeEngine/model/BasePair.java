package com.example.rnapdbeeEngine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for BasePair
 */
public class BasePair {

    @JsonProperty("nt1")
    private Residue nt1;

    @JsonProperty("nt2")
    private Residue nt2;

    @JsonProperty("leontisWesthof")
    private LeontisWesthof leontisWesthof;

    @JsonProperty("saenger")
    private Saenger saenger;

    public Residue getNt1() {
        return nt1;
    }

    public Residue getNt2() {
        return nt2;
    }

    public LeontisWesthof getLeontisWesthof() {
        return leontisWesthof;
    }

    public Saenger getSaenger() {
        return saenger;
    }
}

