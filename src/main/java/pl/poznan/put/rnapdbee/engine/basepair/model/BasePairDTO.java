package pl.poznan.put.rnapdbee.engine.basepair.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.BasePair;

import java.util.Optional;

/**
 * DTO class for BasePair
 * instances of this class are being returned by rnapdbee-adapters as array members.
 */
// TODO ask if the approach of just one class there is good (in my opinion it's not needed to
public class BasePairDTO extends BasePair {

    @JsonProperty("nt1")
    private Residue nt1;

    @JsonProperty("nt2")
    private Residue nt2;

    @JsonProperty("lw")
    private LeontisWesthof leontisWesthof;

    @JsonProperty("saenger")
    private Saenger saenger;

    @JsonProperty("topology")
    private StackingTopology topology;

    @JsonProperty("br")
    private BR br;

    @JsonProperty("bph")
    private BPh bph;

    public Residue getNt1() {
        return nt1;
    }

    public Residue getNt2() {
        return nt2;
    }

    public LeontisWesthof getLeontisWesthof() {
        return leontisWesthof;
    }

    public Saenger getSaenger() {
        return saenger;
    }

    public StackingTopology getTopology() {
        return topology;
    }

    public BR getBr() {
        return br;
    }

    public BPh getBph() {
        return bph;
    }

    @Override
    public PdbNamedResidueIdentifier left() {
        return mapResidueToPdbNamedResidueIdentifier(nt1);
    }
    
    @Override
    public PdbNamedResidueIdentifier right() {
        return mapResidueToPdbNamedResidueIdentifier(nt2);
    }

    private PdbNamedResidueIdentifier mapResidueToPdbNamedResidueIdentifier(Residue residue) {
        return residue.getAuth() != null
                ? ImmutablePdbNamedResidueIdentifier.of(
                residue.getAuth().getChainIdentifier(),
                residue.getAuth().getResidueNumber(),
                Optional.ofNullable(residue.getAuth().getInsertionCode()),
                residue.getAuth().getName().charAt(0))
                : ImmutablePdbNamedResidueIdentifier.of(
                residue.getLabel().getChainIdentifier(),
                residue.getLabel().getResidueNumber(),
                Optional.empty(),
                residue.getLabel().getName().charAt(0));
    }
}

