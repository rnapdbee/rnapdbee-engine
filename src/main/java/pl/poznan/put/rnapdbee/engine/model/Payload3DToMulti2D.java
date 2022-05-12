package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload object of 3D -> multi 2D calculation
 */
public class Payload3DToMulti2D {

    @JsonProperty("fileContent")
    private String fileContent;

    @JsonProperty("modelSelection")
    private ModelSelection modelSelection;

    @JsonProperty("includeNonCanonical")
    private Boolean includeNonCanonical;

    @JsonProperty("removeIsolated")
    private Boolean removeIsolated;

    @JsonProperty("visualizationTool")
    private VisualizationTools visualizationTool;

    public String getFileContent() {
        return fileContent;
    }

    public ModelSelection getModelSelection() {
        return modelSelection;
    }

    public Boolean getIncludeNonCanonical() {
        return includeNonCanonical;
    }

    public Boolean getRemoveIsolated() {
        return removeIsolated;
    }

    public VisualizationTools getVisualizationTool() {
        return visualizationTool;
    }

}
