package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


/**
 * DTO class for AnalysisOutput
 */
public class AnalysisOutput {

    @JsonProperty("basePairs")
    private List<BasePair> basePairs;

    @JsonProperty("stackings")
    private List<Stacking> stackings;

    @JsonProperty("baseRiboseInteractions")
    private List<BaseRibose> baseRiboseInteractions;

    @JsonProperty("basePhosphateInteractions")
    private List<BasePhosphate> basePhosphateInteractions;

    @JsonProperty("other")
    private List<Other> other;

    public List<BasePair> getBasePairs() {
        return basePairs;
    }

    public List<Stacking> getStackings() {
        return stackings;
    }

    public List<BaseRibose> getBaseRiboseInteractions() {
        return baseRiboseInteractions;
    }

    public List<BasePhosphate> getBasePhosphateInteractions() {
        return basePhosphateInteractions;
    }

    public List<Other> getOther() {
        return other;
    }

}
