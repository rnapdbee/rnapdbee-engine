package pl.poznan.put.rnapdbee.engine.basepair.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.BasePair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DTO class for BasePair
 * instances of this class are being returned by rnapdbee-adapters as array members.
 */
public class BasePairDTO extends BasePair {

    private static final Set<String> CANONICAL_ONE_LETTER_NAME_SORTED_PAIRS = new HashSet<>(Arrays.asList("AU", "GU", "CG"));

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
        return saenger != null
                ? saenger
                : Saenger.UNKNOWN;
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

    public boolean isCanonical() {
        if (leontisWesthof == LeontisWesthof.CWW) {
            String sequence = Stream.of(this.left().oneLetterName(), this.right().oneLetterName())
                    .map(c -> Character.toString(c))
                    .map(String::toUpperCase)
                    .sorted().collect(Collectors.joining());

            return CANONICAL_ONE_LETTER_NAME_SORTED_PAIRS.contains(sequence);
        }
        return false;
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

