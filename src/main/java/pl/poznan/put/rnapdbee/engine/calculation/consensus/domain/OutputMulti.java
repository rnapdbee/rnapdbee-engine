package pl.poznan.put.rnapdbee.engine.calculation.consensus.domain;


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

    public String getTitle() {
        return title;
    }

    public ConsensualVisualization getConsensualVisualization() {
        return consensualVisualization;
    }

    private OutputMulti(List<OutputMultiEntry> entries, String title, ConsensualVisualization consensualVisualization) {
        this.entries = entries;
        this.title = title;
        this.consensualVisualization = consensualVisualization;
    }

    public static class OutputMultiBuilder {
        private List<OutputMultiEntry> entries;
        private String title;
        private ConsensualVisualization consensualVisualization;

        public OutputMultiBuilder withEntries(List<OutputMultiEntry> entries) {
            this.entries = entries;
            return this;
        }

        public OutputMultiBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public OutputMultiBuilder withConsensualVisualization(ConsensualVisualization consensualVisualization) {
            this.consensualVisualization = consensualVisualization;
            return this;
        }

        public OutputMulti build() {
            return new OutputMulti(entries, title, consensualVisualization);
        }
    }
}
