package pl.poznan.put.rnapdbee.engine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for Residue
 */
public class Residue {

    @JsonProperty("label")
    private ResidueLabel label;

    @JsonProperty("auth")
    private ResidueAuth auth;

    public ResidueLabel getLabel() {
        return label;
    }

    public ResidueAuth getAuth() {
        return auth;
    }
}
