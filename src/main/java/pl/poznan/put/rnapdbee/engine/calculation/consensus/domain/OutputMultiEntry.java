package pl.poznan.put.rnapdbee.engine.calculation.consensus.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.Output2D;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;

import java.util.List;

/**
 * OutputMultiEntry
 */
public class OutputMultiEntry {

    @JsonProperty("output2D")
    private Output2D output2D;

    @JsonProperty("adapterEnums")
    private List<AnalysisTool> adapterEnums;

    public Output2D getOutput2D() {
        return output2D;
    }

    public OutputMultiEntry withOutput2D(Output2D output2D) {
        this.output2D = output2D;
        return this;
    }

    public List<AnalysisTool> getAdapterEnums() {
        return adapterEnums;
    }

    public OutputMultiEntry withAdapterEnums(List<AnalysisTool> adapterEnums) {
        this.adapterEnums = adapterEnums;
        return this;
    }
    // TODO refactor to builder pattern for consistency of Output2D, Output3D and OutputMulti
}
