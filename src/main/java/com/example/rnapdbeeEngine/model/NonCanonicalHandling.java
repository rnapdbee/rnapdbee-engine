package com.example.rnapdbeeEngine.model;


/**
 * Enum for NonCanonicalHandling
 */
public enum NonCanonicalHandling {

    VISUALIZATION_ONLY("VisualizationOnly"),
    TEXT_AND_VISUALIZATION("TextAndVisualization"),
    IGNORE("Ignore");

    private String value;

    NonCanonicalHandling(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
