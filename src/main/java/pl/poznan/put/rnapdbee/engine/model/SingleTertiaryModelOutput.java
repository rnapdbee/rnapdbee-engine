package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.engine.calculation.model.Output2D;

import java.util.List;

/**
 * DTO class for SingleTertiaryModelOutput
 */
public class SingleTertiaryModelOutput {


    @JsonProperty("title")
    private String title;

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

    @JsonProperty("stackingInteractions")
    private List<OutputBasePair> stackingInteractions;

    @JsonProperty("basePhosphateInteractions")
    private List<OutputBasePair> basePhosphateInteractions;

    @JsonProperty("baseRiboseInteractions")
    private List<OutputBasePair> baseRiboseInteractions;

    public Output2D getOutput2D() {
        return output2D;
    }

    public void setOutput2D(Output2D output2D) {
        this.output2D = output2D;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<OutputBasePair> getCanonicalInteractions() {
        return canonicalInteractions;
    }

    public void setCanonicalInteractions(List<OutputBasePair> canonicalInteractions) {
        this.canonicalInteractions = canonicalInteractions;
    }

    public List<OutputBasePair> getNonCanonicalInteractions() {
        return nonCanonicalInteractions;
    }

    public void setNonCanonicalInteractions(List<OutputBasePair> nonCanonicalInteractions) {
        this.nonCanonicalInteractions = nonCanonicalInteractions;
    }

    public List<OutputBasePair> getStackingInteractions() {
        return stackingInteractions;
    }

    public void setStackingInteractions(List<OutputBasePair> stackingInteractions) {
        this.stackingInteractions = stackingInteractions;
    }

    public List<OutputBasePair> getBasePhosphateInteractions() {
        return basePhosphateInteractions;
    }

    public void setBasePhosphateInteractions(List<OutputBasePair> basePhosphateInteractions) {
        this.basePhosphateInteractions = basePhosphateInteractions;
    }

    public List<OutputBasePair> getBaseRiboseInteractions() {
        return baseRiboseInteractions;
    }

    public void setBaseRiboseInteractions(List<OutputBasePair> baseRiboseInteractions) {
        this.baseRiboseInteractions = baseRiboseInteractions;
    }

    public Integer getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(Integer modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
