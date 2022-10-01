package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for Consensus Visualization - currently blank in the spec
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

    public ConsensualVisualization() {
    }

    public ConsensualVisualization(String pathToPNGImage, String pathToSVGImage) {
        this.pathToPngImage = pathToPNGImage;
        this.pathToSvgImage = pathToSVGImage;
    }
}
