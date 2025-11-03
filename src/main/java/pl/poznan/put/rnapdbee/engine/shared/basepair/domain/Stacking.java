package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for Stacking
 */

public class Stacking {

    @JsonProperty("nt1")
    private Residue nt1;

    @JsonProperty("nt2")
    private Residue nt2;

    @JsonProperty("topology")
    private StackingTopology topology;

    public Stacking() {
    }

    public Residue getNt1() {
        return nt1;
    }

    public Residue getNt2() {
        return nt2;
    }

    public StackingTopology getTopology() {
        return topology;
    }

    public void setNt1(Residue nt1) {
        this.nt1 = nt1;
    }

    public void setNt2(Residue nt2) {
        this.nt2 = nt2;
    }

    public void setTopology(StackingTopology topology) {
        this.topology = topology;
    }
}
