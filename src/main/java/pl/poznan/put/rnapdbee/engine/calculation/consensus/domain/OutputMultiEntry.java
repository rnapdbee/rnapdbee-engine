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

    public List<AnalysisTool> getAdapterEnums() {
        return adapterEnums;
    }

    private OutputMultiEntry(Output2D output2D, List<AnalysisTool> adapterEnums) {
        this.output2D = output2D;
        this.adapterEnums = adapterEnums;
    }


    public static class OutputMultiEntryBuilder {
        private Output2D output2D;
        private List<AnalysisTool> adapterEnums;

        public OutputMultiEntryBuilder withOutput2D(Output2D output2D) {
            this.output2D = output2D;
            return this;
        }

        public OutputMultiEntryBuilder withAdapterEnums(List<AnalysisTool> adapterEnums) {
            this.adapterEnums = adapterEnums;
            return this;
        }

        public OutputMultiEntry build() {
            return new OutputMultiEntry(output2D, adapterEnums);
        }
    }
}
