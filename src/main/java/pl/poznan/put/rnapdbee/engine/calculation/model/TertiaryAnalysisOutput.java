package pl.poznan.put.rnapdbee.engine.calculation.model;

import edu.put.rnapdbee.analysis.AnalysisOutput;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO class representing response body of (...) -> image processing
 */
public class TertiaryAnalysisOutput {

    private final List<SingleTertiaryAnalysisOutput> analysis;

    public TertiaryAnalysisOutput(List<AnalysisOutput> analysisOutputs) {
        this.analysis = analysisOutputs
                .stream()
                .map(SingleTertiaryAnalysisOutput::new).collect(Collectors.toList());
    }

    public List<SingleTertiaryAnalysisOutput> getAnalysis() {
        return analysis;
    }
}
