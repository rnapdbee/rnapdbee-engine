package com.example.rnapdbeeEngine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for OutputImage
 */
public class OutputImage {
    @JsonProperty("visualization")
    private Visualization visualization;

    public Visualization getVisualization() {
        return visualization;
    }
}
