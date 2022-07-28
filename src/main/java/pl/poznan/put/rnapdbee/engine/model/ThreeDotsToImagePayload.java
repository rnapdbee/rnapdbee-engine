package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;

/**
 * Payload of (...) -> Image calculation
 */
public class ThreeDotsToImagePayload {

    @JsonProperty("fileContent")
    private String fileContent;

    @JsonProperty("structuralElementsHandling")
    private StructuralElementsHandling structuralElementsHandling;

    @JsonProperty("visualizationTool")
    private VisualizationTool visualizationTool;

    public String getFileContent() {
        return fileContent;
    }

    public StructuralElementsHandling getStructuralElementsHandling() {
        return structuralElementsHandling;
    }

    public VisualizationTool getVisualizationTool() {
        return visualizationTool;
    }

}

