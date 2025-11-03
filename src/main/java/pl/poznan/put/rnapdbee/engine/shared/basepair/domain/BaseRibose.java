package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for BaseRibose
 */
public class BaseRibose {

    @JsonProperty("nt1")
    private Residue nt1;

    @JsonProperty("nt2")
    private Residue nt2;

    @JsonProperty("br")
    private BaseRiboseType br;

    public BaseRibose() {
    }

    public Residue getNt1() {
        return nt1;
    }

    public Residue getNt2() {
        return nt2;
    }

    public BaseRiboseType getBr() {
        return br;
    }

    public void setNt1(Residue nt1) {
        this.nt1 = nt1;
    }

    public void setNt2(Residue nt2) {
        this.nt2 = nt2;
    }

    public void setBr(BaseRiboseType br) {
        this.br = br;
    }
}
