package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload object of 2D -> (...) calculation
 */
public class Payload2DToThreeDots {

    @JsonProperty("fileContent")
    private String fileContent;

    @JsonProperty("removeIsolated")
    private Boolean removeIsolated;

    @JsonProperty("structuralElementsHandling")
    private StructuralElementsHandling structuralElementsHandling;

    @JsonProperty("visualizationTool")
    private VisualizationTool visualizationTool;

    public String getFileContent() {
        return fileContent;
    }

    public Boolean getRemoveIsolated() {
        return removeIsolated;
    }

    public StructuralElementsHandling getStructuralElementsHandling() {
        return structuralElementsHandling;
    }

    public VisualizationTool getVisualizationTool() {
        return visualizationTool;
    }

}
