package com.example.rnapdbeeEngine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OutputMultiEntry
 */
public class OutputMultiEntry {

    @JsonProperty("output2D")
    private Output2D output2D;

    @JsonProperty("consensusVisualization")
    private ConsensusVisualization consensusVisualization;

}
