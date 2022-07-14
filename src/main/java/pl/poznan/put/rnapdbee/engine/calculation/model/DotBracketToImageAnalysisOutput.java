package pl.poznan.put.rnapdbee.engine.calculation.model;

import edu.put.rnapdbee.analysis.AnalysisOutput;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO class representing response body of (...) -> image processing
 */
public class DotBracketToImageAnalysisOutput {

    private final List<SingleDotBracketToImageAnalysisOutput> analysis;

    public DotBracketToImageAnalysisOutput(List<AnalysisOutput> analysisOutputs) {
        this.analysis = analysisOutputs
                .stream()
                .map(SingleDotBracketToImageAnalysisOutput::new).collect(Collectors.toList());
    }

    public List<SingleDotBracketToImageAnalysisOutput> getAnalysis() {
        return analysis;
    }
}
