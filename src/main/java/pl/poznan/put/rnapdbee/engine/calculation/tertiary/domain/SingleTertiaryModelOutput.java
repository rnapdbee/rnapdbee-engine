package pl.poznan.put.rnapdbee.engine.calculation.tertiary.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.Output2D;
import pl.poznan.put.structure.AnalyzedBasePair;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO class for SingleTertiaryModelOutput
 */
public class SingleTertiaryModelOutput {

    @JsonProperty("modelNumber")
    private Integer modelNumber;

    @JsonProperty("output2D")
    private Output2D output2D;

    @JsonProperty("messages")
    private List<String> messages;

    @JsonProperty("canonicalInteractions")
    private List<OutputBasePair> canonicalInteractions;

    @JsonProperty("nonCanonicalInteractions")
    private List<OutputBasePair> nonCanonicalInteractions;

    @JsonProperty("interStrandInteractions")
    private List<OutputBasePair> interStrandInteractions;

    @JsonProperty("stackingInteractions")
    private List<OutputBasePair> stackingInteractions;

    @JsonProperty("basePhosphateInteractions")
    private List<OutputBasePair> basePhosphateInteractions;

    @JsonProperty("baseRiboseInteractions")
    private List<OutputBasePair> baseRiboseInteractions;

    public Output2D getOutput2D() {
        return output2D;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<OutputBasePair> getCanonicalInteractions() {
        return canonicalInteractions;
    }

    public List<OutputBasePair> getNonCanonicalInteractions() {
        return nonCanonicalInteractions;
    }

    public List<OutputBasePair> getInterStrandInteractions() {
        return interStrandInteractions;
    }

    public List<OutputBasePair> getStackingInteractions() {
        return stackingInteractions;
    }

    public List<OutputBasePair> getBasePhosphateInteractions() {
        return basePhosphateInteractions;
    }

    public List<OutputBasePair> getBaseRiboseInteractions() {
        return baseRiboseInteractions;
    }

    public Integer getModelNumber() {
        return modelNumber;
    }

    private SingleTertiaryModelOutput(Integer modelNumber,
                                      Output2D output2D,
                                      List<String> messages,
                                      List<OutputBasePair> canonicalInteractions,
                                      List<OutputBasePair> nonCanonicalInteractions,
                                      List<OutputBasePair> interStrandInteractions,
                                      List<OutputBasePair> stackingInteractions,
                                      List<OutputBasePair> basePhosphateInteractions,
                                      List<OutputBasePair> baseRiboseInteractions) {
        this.modelNumber = modelNumber;
        this.output2D = output2D;
        this.messages = messages;
        this.canonicalInteractions = canonicalInteractions;
        this.nonCanonicalInteractions = nonCanonicalInteractions;
        this.interStrandInteractions = interStrandInteractions;
        this.stackingInteractions = stackingInteractions;
        this.basePhosphateInteractions = basePhosphateInteractions;
        this.baseRiboseInteractions = baseRiboseInteractions;
    }

    public static class Builder {
        private Integer modelNumber;
        private Output2D output2D;
        private List<String> messages;
        private List<OutputBasePair> canonicalInteractions;
        private List<OutputBasePair> nonCanonicalInteractions;
        private List<OutputBasePair> interStrandInteractions;
        private List<OutputBasePair> stackingInteractions;
        private List<OutputBasePair> basePhosphateInteractions;
        private List<OutputBasePair> baseRiboseInteractions;

        public Builder withModelNumber(Integer modelNumber) {
            this.modelNumber = modelNumber;
            return this;
        }

        public Builder withOutput2D(Output2D output2D) {
            this.output2D = output2D;
            return this;
        }

        public Builder withMessages(List<String> messages) {
            this.messages = messages;
            return this;
        }

        public Builder withCanonicalInteractions(List<AnalyzedBasePair> canonicalInteractions) {
            this.canonicalInteractions = canonicalInteractions.stream()
                    .map(OutputBasePair::fromClassifiedBasePair)
                    .collect(Collectors.toList());
            return this;
        }

        public Builder withNonCanonicalInteractions(List<AnalyzedBasePair> nonCanonicalInteractions) {
            this.nonCanonicalInteractions = nonCanonicalInteractions.stream()
                    .map(OutputBasePair::fromClassifiedBasePair)
                    .collect(Collectors.toList());
            return this;
        }

        public Builder withInterStrandInteractions(List<AnalyzedBasePair> interStrandInteractions) {
            this.interStrandInteractions = interStrandInteractions.stream()
                    .map(OutputBasePair::fromClassifiedBasePair)
                    .collect(Collectors.toList());
            return this;
        }

        public Builder withStackingInteractions(List<AnalyzedBasePair> stackingInteractions) {
            this.stackingInteractions = stackingInteractions.stream()
                    .map(OutputBasePair::fromClassifiedBasePair)
                    .collect(Collectors.toList());
            return this;
        }

        public Builder withBasePhosphateInteractions(List<AnalyzedBasePair> basePhosphateInteractions) {
            this.basePhosphateInteractions = basePhosphateInteractions.stream()
                    .map(OutputBasePair::fromClassifiedBasePair)
                    .collect(Collectors.toList());
            return this;
        }

        public Builder withBaseRiboseInteractions(List<AnalyzedBasePair> baseRiboseInteractions) {
            this.baseRiboseInteractions = baseRiboseInteractions.stream()
                    .map(OutputBasePair::fromClassifiedBasePair)
                    .collect(Collectors.toList());
            return this;
        }

        public SingleTertiaryModelOutput build() {
            return new SingleTertiaryModelOutput(modelNumber,
                    output2D,
                    messages,
                    canonicalInteractions,
                    nonCanonicalInteractions,
                    interStrandInteractions,
                    stackingInteractions,
                    basePhosphateInteractions,
                    baseRiboseInteractions);
        }
    }
}
