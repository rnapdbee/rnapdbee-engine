package pl.poznan.put.rnapdbee.engine.model;


/**
 * Enum for NonCanonicalHandling
 */
public enum NonCanonicalHandling {

    VISUALIZATION_ONLY,
    TEXT_AND_VISUALIZATION,
    IGNORE;

    // TODO remove when rnapdbee-common is merged to this code
    public edu.put.rnapdbee.enums.NonCanonicalHandling mapTo2_0Enum() {
        switch(this) {
            case IGNORE:
                return edu.put.rnapdbee.enums.NonCanonicalHandling.IGNORE;
            case VISUALIZATION_ONLY:
                return edu.put.rnapdbee.enums.NonCanonicalHandling.ONLY_VISUALIZE;
            case TEXT_AND_VISUALIZATION:
                return edu.put.rnapdbee.enums.NonCanonicalHandling.ANALYZE_VISUALIZE;
            default:
                throw new RuntimeException("unhandled NonCanonicalHandling enum");
        }
    }

}
