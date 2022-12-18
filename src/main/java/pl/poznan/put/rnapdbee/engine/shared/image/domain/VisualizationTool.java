package pl.poznan.put.rnapdbee.engine.shared.image.domain;


/**
 * enum for VisualizationTools
 */
public enum VisualizationTool {

    VARNA,
    PSEUDO_VIEWER,
    R_CHIE,
    RNA_PUZZLER,
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
            case RNA_PUZZLER:
                return VARNA;
            case VARNA:
                return RNA_PUZZLER;
            case NONE:
            default:
                return NONE;
        }
    }
}
