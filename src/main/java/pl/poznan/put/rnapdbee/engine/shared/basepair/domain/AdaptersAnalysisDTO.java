package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


/**
 * DTO class for AdaptersAnalysisDTO
 */
public class AdaptersAnalysisDTO {

    @JsonProperty("basePairs")
    private List<BasePairDTO> basePairs;

    @JsonProperty("stackings")
    private List<BasePairDTO> stackings;

    @JsonProperty("baseRiboseInteractions")
    private List<BasePairDTO> baseRiboseInteractions;

    @JsonProperty("basePhosphateInteractions")
    private List<BasePairDTO> basePhosphateInteractions;

    @JsonProperty("otherInteractions")
    private List<BasePairDTO> other;

    public List<BasePairDTO> getBasePairs() {
        return basePairs;
    }

    public List<BasePairDTO> getStackings() {
        return stackings;
    }

    public List<BasePairDTO> getBaseRiboseInteractions() {
        return baseRiboseInteractions;
    }

    public List<BasePairDTO> getBasePhosphateInteractions() {
        return basePhosphateInteractions;
    }

    public List<BasePairDTO> getOther() {
        return other;
    }

}
