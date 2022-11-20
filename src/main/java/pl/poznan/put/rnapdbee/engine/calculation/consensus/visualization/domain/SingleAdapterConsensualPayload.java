package pl.poznan.put.rnapdbee.engine.calculation.consensus.visualization.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.SingleStrandOutput;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;

import java.util.List;

public class SingleAdapterConsensualPayload {

    @JsonProperty("adapter")
    private AnalysisTool adapter;

    @JsonProperty("strands")
    private List<SingleStrandOutput> strands;

    public SingleAdapterConsensualPayload(AnalysisTool adapter, List<SingleStrandOutput> strands) {
        this.adapter = adapter;
        this.strands = strands;
    }

    public AnalysisTool getAdapter() {
        return adapter;
    }

    public List<SingleStrandOutput> getStrands() {
        return strands;
    }
}
