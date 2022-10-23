package pl.poznan.put.rnapdbee.engine.shared.image.domain;


/**
 * enum for VisualizationTools
 */
public enum VisualizationTool {

    VARNA,
    PSEUDO_VIEWER,
    R_CHIE,
    RNA_GLIB,
    FORNA,
    RNA_PUZZLER,
    RNA_TURTLE,
    RNA_TRAVELER,
    BARNABA,
    NONE;

    /**
     * method returning backup visualization tool for each tool.
     *
     * @return Visualization Tool
     */
    public VisualizationTool getBackupVisualizationTool() {
        switch (this) {
            case PSEUDO_VIEWER:
            case R_CHIE:
                return VARNA;
            case VARNA:
                return PSEUDO_VIEWER;
            case NONE:
            default:
                return NONE;
        }
    }
}
