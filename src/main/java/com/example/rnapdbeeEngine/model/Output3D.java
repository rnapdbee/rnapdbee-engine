package com.example.rnapdbeeEngine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for Output3D
 */
public class Output3D {

    @JsonProperty("analysisOutput")
    private AnalysisOutput analysisOutput;

    @JsonProperty("output2D")
    private Output2D output2D;

}
