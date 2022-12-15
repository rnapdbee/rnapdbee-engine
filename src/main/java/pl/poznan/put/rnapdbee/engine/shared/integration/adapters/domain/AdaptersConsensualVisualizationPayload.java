package pl.poznan.put.rnapdbee.engine.shared.integration.adapters.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMultiEntry;

import java.util.List;
import java.util.stream.Collectors;

public class AdaptersConsensualVisualizationPayload {

    @JsonProperty("results")
    private List<SingleAdapterConsensualPayload> results;

    private AdaptersConsensualVisualizationPayload(List<SingleAdapterConsensualPayload> results) {
        this.results = results;
    }

    public static AdaptersConsensualVisualizationPayload of(List<OutputMultiEntry> outputMultiEntries) {
        List<SingleAdapterConsensualPayload> adapterConsensualPayloads = outputMultiEntries.stream()
                .flatMap(entry -> entry.getAdapterEnums().stream()
                        .map(adapterEnum ->
                                new SingleAdapterConsensualPayload(adapterEnum, entry.getOutput2D().getStrands())))
                .collect(Collectors.toList());

        return new AdaptersConsensualVisualizationPayload(adapterConsensualPayloads);
    }

    public List<SingleAdapterConsensualPayload> getResults() {
        return results;
    }
}
