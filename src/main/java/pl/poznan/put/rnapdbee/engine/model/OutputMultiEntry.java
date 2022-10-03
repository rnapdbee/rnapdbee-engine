package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.engine.calculation.model.Output2D;

import java.util.List;

/**
 * OutputMultiEntry
 */
public class OutputMultiEntry {

    @JsonProperty("output2D")
    private Output2D output2D;

    @JsonProperty("adapterEnums")
    private List<String> adapterEnums;

    public Output2D getOutput2D() {
        return output2D;
    }

    public OutputMultiEntry withOutput2D(Output2D output2D) {
        this.output2D = output2D;
        return this;
    }

    public List<String> getAdapterEnums() {
        return adapterEnums;
    }

    public OutputMultiEntry withAdapterEnums(List<String> adapterEnums) {
        this.adapterEnums = adapterEnums;
        return this;
    }
}
