package pl.poznan.put.rnapdbee.engine.shared.domain;


/**
 * Enum for NonCanonicalHandling
 */
public enum NonCanonicalHandling {

    TEXT_AND_VISUALIZATION(true, true),
    VISUALIZATION_ONLY(false, true),
    IGNORE(false, false);

    private final boolean isAnalysis;
    private final boolean isVisualization;

    NonCanonicalHandling(final boolean isAnalysis, final boolean isVisualization) {
        this.isAnalysis = isAnalysis;
        this.isVisualization = isVisualization;
    }

    public boolean isAnalysis() {
        return isAnalysis;
    }

    public boolean isVisualization() {
        return isVisualization;
    }

    public String getArchiveName() {
        if (isAnalysis && isVisualization) {
            return "nc_all";
        }
        if (isAnalysis) {
            return "nc_analysis";
        }
        if (isVisualization) {
            return "nc_visualization";
        }
        return "";
    }

}
