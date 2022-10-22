package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


/**
 * DTO class for OutputMulti
 */
public class OutputMulti {

    @JsonProperty("entries")
    private List<OutputMultiEntry> entries;

    @JsonProperty("title")
    private String title;

    /* TODO: here should be the consensual visualization written as weblogo (separate task) */
    @JsonProperty("consensualVisualization")
    private ConsensualVisualization consensualVisualization;

    public List<OutputMultiEntry> getEntries() {
        return entries;
    }

    public OutputMulti withEntries(List<OutputMultiEntry> entries) {
        this.entries = entries;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public OutputMulti withTitle(String title) {
        this.title = title;
        return this;
    }

    public ConsensualVisualization getConsensualVisualization() {
        return consensualVisualization;
    }

    public OutputMulti withConsensualVisualization(ConsensualVisualization consensualVisualization) {
        this.consensualVisualization = consensualVisualization;
        return this;
    }
}
