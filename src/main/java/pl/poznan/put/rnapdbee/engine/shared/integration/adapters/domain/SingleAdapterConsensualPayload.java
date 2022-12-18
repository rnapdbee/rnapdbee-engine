package pl.poznan.put.rnapdbee.engine.shared.integration.adapters.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.SingleStrandOutput;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;

import java.util.List;
import java.util.stream.Collectors;

public class SingleAdapterConsensualPayload {

    @JsonProperty("adapter")
    private AnalysisTool adapter;

    @JsonProperty("strands")
    private List<StrandPayload> strands;

    private SingleAdapterConsensualPayload(AnalysisTool adapter, List<StrandPayload> strands) {
        this.adapter = adapter;
        this.strands = strands;
    }

    public static SingleAdapterConsensualPayload of(AnalysisTool adapter, List<SingleStrandOutput> strands) {
        List<StrandPayload> strandPayloads = strands.stream()
                .map(StrandPayload::ofStrandOutput)
                .collect(Collectors.toList());
        return new SingleAdapterConsensualPayload(adapter, strandPayloads);
    }

    public AnalysisTool getAdapter() {
        return adapter;
    }

    public List<StrandPayload> getStrands() {
        return strands;
    }
}
