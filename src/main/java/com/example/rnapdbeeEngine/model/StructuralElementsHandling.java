package com.example.rnapdbeeEngine.model;


/**
 * enum for StructuralElementsHandling
 */
public enum StructuralElementsHandling {

    USE_PSEUDO_KNOTS("UsePseudoknots"),

    IGNORE_PSEUDOKNOTS("IgnorePseudoknots");

    private String value;

    StructuralElementsHandling(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
