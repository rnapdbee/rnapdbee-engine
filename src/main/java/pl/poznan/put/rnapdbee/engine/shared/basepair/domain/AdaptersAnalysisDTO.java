package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;

/**
 * DTO class for AdaptersAnalysisDTO
 */
public class AdaptersAnalysisDTO {

    @JsonProperty("base_pairs")
    private List<BasePairDTO> basePairs;

    @JsonProperty("stackings")
    private List<BasePairDTO> stackings;

    @JsonProperty("base_ribose_interactions")
    private List<BasePairDTO> baseRiboseInteractions;

    @JsonProperty("base_phosphate_interactions")
    private List<BasePairDTO> basePhosphateInteractions;

    @JsonProperty("other_interactions")
    private List<BasePairDTO> other;

    public AdaptersAnalysisDTO() {
    }

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

    public void setBasePairs(List<BasePairDTO> basePairs) {
        this.basePairs = basePairs;
    }

    public void setStackings(List<BasePairDTO> stackings) {
        this.stackings = stackings;
    }

    public void setBaseRiboseInteractions(List<BasePairDTO> baseRiboseInteractions) {
        this.baseRiboseInteractions = baseRiboseInteractions;
    }

    public void setBasePhosphateInteractions(List<BasePairDTO> basePhosphateInteractions) {
        this.basePhosphateInteractions = basePhosphateInteractions;
    }

    public void setOther(List<BasePairDTO> other) {
        this.other = other;
    }
}
