package pl.poznan.put.rnapdbee.engine.calculation.consensus.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for Consensual Visualization
 */
public class ConsensualVisualization {

    @JsonProperty("svgFile")
    private byte[] svgFile;

    public ConsensualVisualization(byte[] svgFile) {
        this.svgFile = svgFile;
    }

    public byte[] getSvgFile() {
        return svgFile;
    }

    public ConsensualVisualization setSvgFile(byte[] svgFile) {
        this.svgFile = svgFile;
        return this;
    }
}
