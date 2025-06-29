package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for BasePhosphate
 */
public class BasePhosphate {
    @JsonProperty("nt1")
    private Residue nt1;

    @JsonProperty("nt2")
    private Residue nt2;

    @JsonProperty("bph")
    private BasePhosphateType bph;

    public Residue getNt1() {
        return nt1;
    }

    public Residue getNt2() {
        return nt2;
    }

    public BasePhosphateType getBph() {
        return bph;
    }

}
