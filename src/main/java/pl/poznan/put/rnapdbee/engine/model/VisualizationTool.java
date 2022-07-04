package pl.poznan.put.rnapdbee.engine.model;


/**
 * enum for VisualizationTools
 */
public enum VisualizationTool {
  
  VARNA("VARNA"),
  PSEUDOVIEWER("PseudoViewer"),
  R_CHIE("R-Chie"),
  RNAGLIB("RNAglib"),
  FORNA("forna"),
  RNAPUZZLER("RNApuzzler"),
  RNATURTLE("RNAturtle"),
  RNATRAVELER("RNAtraveler"),
  BARNABA("baRNAba"),
  NONE("None");

  private String value;

  VisualizationTool(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

}
