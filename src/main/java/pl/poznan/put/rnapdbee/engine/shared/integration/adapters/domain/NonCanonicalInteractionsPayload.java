package pl.poznan.put.rnapdbee.engine.shared.integration.adapters.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.structure.ClassifiedBasePair;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NonCanonicalInteractionsPayload {

    @JsonProperty
    private List<PairPayload> represented;

    @JsonProperty
    private List<PairPayload> notRepresented;

    public static final NonCanonicalInteractionsPayload EMPTY_PAYLOAD = new
            NonCanonicalInteractionsPayload(Collections.emptyList(), Collections.emptyList());

    public List<PairPayload> getRepresented() {
        return represented;
    }

    public List<PairPayload> getNotRepresented() {
        return notRepresented;
    }

    public static NonCanonicalInteractionsPayload of(List<? extends ClassifiedBasePair> basePairs) {
        var groups = basePairs.stream()
                .collect(Collectors.partitioningBy(ClassifiedBasePair::isRepresented));

        List<PairPayload> representedPairs = groups
                .get(true).stream()
                .map(PairPayload::of).collect(Collectors.toList());
        List<PairPayload> notRepresentedPairs = groups
                .get(false).stream()
                .map(PairPayload::of).collect(Collectors.toList());

        return new NonCanonicalInteractionsPayload(representedPairs, notRepresentedPairs);
    }

    private NonCanonicalInteractionsPayload(List<PairPayload> represented,
                                            List<PairPayload> notRepresented) {
        this.notRepresented = notRepresented;
        this.represented = represented;
    }
}
