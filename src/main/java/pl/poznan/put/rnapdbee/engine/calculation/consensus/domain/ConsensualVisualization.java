package pl.poznan.put.rnapdbee.engine.calculation.consensus.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for Consensual Visualization
 */
public class ConsensualVisualization {

    @JsonProperty("pathToPngImage")
    private String pathToPngImage;
    @JsonProperty("pathToSvgImage")
    private String pathToSvgImage;

    public void setPathToPNGImage(String pathToPNGImage) {
        this.pathToPngImage = pathToPNGImage;
    }

    public void setPathToSVGImage(String pathToSVGImage) {
        this.pathToSvgImage = pathToSVGImage;
    }

    public String getPathToPngImage() {
        return pathToPngImage;
    }

    public String getPathToSvgImage() {
        return pathToSvgImage;
    }

    public ConsensualVisualization() {
    }

    public ConsensualVisualization(String pathToPNGImage, String pathToSVGImage) {
        this.pathToPngImage = pathToPNGImage;
        this.pathToSvgImage = pathToSVGImage;
    }
}
