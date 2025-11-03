package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for Other
 */
public class Other {

    @JsonProperty("nt1")
    private Residue nt1;

    @JsonProperty("nt2")
    private Residue nt2;

    public Other() {
    }

    public Residue getNt1() {
        return nt1;
    }

    public Residue getNt2() {
        return nt2;
    }

    public void setNt1(Residue nt1) {
        this.nt1 = nt1;
    }

    public void setNt2(Residue nt2) {
        this.nt2 = nt2;
    }
}
