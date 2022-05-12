package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


/**
 * DTO class for Residue
 */
public class Residue {

    @JsonProperty("label")
    private ResidueLabel label;

    @JsonProperty("auth")
    private ResidueAuth auth;

    @JsonProperty("atoms")
    private List<Atom> atoms = new ArrayList<>();

    public ResidueLabel getLabel() {
        return label;
    }

    public ResidueAuth getAuth() {
        return auth;
    }

    public List<Atom> getAtoms() {
        return atoms;
    }

}
