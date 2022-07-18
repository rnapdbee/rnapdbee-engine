package pl.poznan.put.rnapdbee.engine.calculation.model;


import java.util.List;

/**
 * DTO class representing response body of (...) -> image processing
 */
public class DotBracketToImageAnalysisOutput {

    private List<SingleDotBracketToImageAnalysisOutput> analysis;

    public List<SingleDotBracketToImageAnalysisOutput> getAnalysis() {
        return analysis;
    }

    public DotBracketToImageAnalysisOutput withAnalysis(List<SingleDotBracketToImageAnalysisOutput> analysis) {
        this.analysis = analysis;
        return this;
    }
}
