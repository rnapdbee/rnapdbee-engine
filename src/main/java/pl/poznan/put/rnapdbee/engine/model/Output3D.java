package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.engine.calculation.model.Output2D;

/**
 * DTO class for Output3D
 */
public class Output3D {

    @JsonProperty("analysisOutput")
    private AnalysisOutput analysisOutput;

    @JsonProperty("output2D")
    private Output2D output2D;

    public AnalysisOutput getAnalysisOutput() {
        return analysisOutput;
    }

    public Output2D getOutput2D() {
        return output2D;
    }
}
