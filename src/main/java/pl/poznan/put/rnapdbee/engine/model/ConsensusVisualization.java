package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for Consensus Visualization - currently blank in the spec
 */
public class ConsensusVisualization {

    @JsonProperty("pathToPngImage")
    private String pathToPngImage;
    @JsonProperty("pathToSvgImage")
    private String pathToSvgImage;

    public String getPathToPNGImage() {
        return pathToPngImage;
    }

    public void setPathToPNGImage(String pathToPNGImage) {
        this.pathToPngImage = pathToPNGImage;
    }

    public String getPathToSVGImage() {
        return pathToSvgImage;
    }

    public void setPathToSVGImage(String pathToSVGImage) {
        this.pathToSvgImage = pathToSVGImage;
    }

    public ConsensusVisualization(String pathToPNGImage, String pathToSVGImage) {
        this.pathToPngImage = pathToPNGImage;
        this.pathToSvgImage = pathToSVGImage;
    }
}
