package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload object of 3D -> (...) calculation
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Payload3DThreeDots {

  @JsonProperty("fileContent")
  private String fileContent;

  @JsonProperty("modelSelection")
  private ModelSelection modelSelection;

  @JsonProperty("analysisTool")
  private AnalysisTool analysisTool;

  @JsonProperty("nonCanonicalHandling")
  private NonCanonicalHandling nonCanonicalHandling;

  @JsonProperty("removeIsolated")
  private Boolean removeIsolated;

  @JsonProperty("structuralElementsHandling")
  private StructuralElementsHandling structuralElementsHandling;

  @JsonProperty("visualizationTool")
  private VisualizationTools visualizationTool;

  public String getFileContent() {
    return fileContent;
  }

  public ModelSelection getModelSelection() {
    return modelSelection;
  }

  public AnalysisTool getAnalysisTool() {
    return analysisTool;
  }

  public NonCanonicalHandling getNonCanonicalHandling() {
    return nonCanonicalHandling;
  }

  public Boolean getRemoveIsolated() {
    return removeIsolated;
  }

  public StructuralElementsHandling getStructuralElementsHandling() {
    return structuralElementsHandling;
  }

  public VisualizationTools getVisualizationTool() {
    return visualizationTool;
  }

}
