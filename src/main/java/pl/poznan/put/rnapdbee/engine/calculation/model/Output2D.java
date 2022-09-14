package pl.poznan.put.rnapdbee.engine.calculation.model;


import java.util.List;

/**
 * DTO class representing response body secondary structure analysis.
 * Relevant for "(...) -> image" and "2D -> (...)" scenarios.
 */
// TODO:
//  There should not be an array of analysis here, only single one. To be discussed on how should it be handled
public class Output2D {

    private List<SingleSecondaryModelAnalysisOutput> analysis;

    public List<SingleSecondaryModelAnalysisOutput> getAnalysis() {
        return analysis;
    }

    public Output2D withAnalysis(List<SingleSecondaryModelAnalysisOutput> analysis) {
        this.analysis = analysis;
        return this;
    }
}
