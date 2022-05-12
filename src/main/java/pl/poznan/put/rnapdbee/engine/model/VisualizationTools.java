package pl.poznan.put.rnapdbee.engine.model;


/**
 * enum for VisualizationTools
 */
public enum VisualizationTools {
  
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

  VisualizationTools(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

}
