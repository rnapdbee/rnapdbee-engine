package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.ChainNumberKey;
import pl.poznan.put.structure.ImmutableBasePair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DTO class for BasePair
 * instances of this class are being returned by rnapdbee-adapters as array members.
 */
public class BasePairDTO {

    private static final Set<String> CANONICAL_ONE_LETTER_NAME_SORTED_PAIRS = new HashSet<>(Arrays.asList("AU", "GU", "CG"));

    @JsonProperty("nt1")
    private Residue nt1;

    @JsonProperty("nt2")
    private Residue nt2;

    @JsonProperty("lw")
    private LeontisWesthofType leontisWesthofType;

    @JsonProperty("saenger")
    private SaengerType saengerType;

    @JsonProperty("topology")
    private StackingTopology topology;

    @JsonProperty("br")
    private BaseRiboseType br;

    @JsonProperty("bph")
    private BasePhosphateType bph;

    public BasePairDTO() {
    }

    private BasePairDTO(BasePairDTO basePairDTO) {
        this.nt1 = basePairDTO.getNt1();
        this.nt2 = basePairDTO.getNt2();
        this.leontisWesthofType = basePairDTO.getLeontisWesthofType();
        this.saengerType = basePairDTO.getSaengerType();
        this.topology = basePairDTO.getTopology();
        this.br = basePairDTO.getBr();
        this.bph = basePairDTO.getBph();
    }

    public Residue getNt1() {
        return nt1;
    }

    public void setNt1(Residue nt1) {
        this.nt1 = nt1;
    }

    public Residue getNt2() {
        return nt2;
    }

    public void setNt2(Residue nt2) {
        this.nt2 = nt2;
    }

    public LeontisWesthofType getLeontisWesthofType() {
        return leontisWesthofType;
    }

    public void setLeontisWesthofType(LeontisWesthofType leontisWesthofType) {
        this.leontisWesthofType = leontisWesthofType;
    }

    public SaengerType getSaengerType() {
        return saengerType;
    }

    public void setSaengerType(SaengerType saengerType) {
        this.saengerType = saengerType;
    }

    public StackingTopology getTopology() {
        return topology;
    }

    public void setTopology(StackingTopology topology) {
        this.topology = topology;
    }

    public BaseRiboseType getBr() {
        return br;
    }

    public void setBr(BaseRiboseType br) {
        this.br = br;
    }

    public BasePhosphateType getBph() {
        return bph;
    }

    public void setBph(BasePhosphateType bph) {
        this.bph = bph;
    }

    public static BasePairDTO ofBasePairDTOWithNameFromMap(BasePairDTO basePairDTO,
                                                           Map<ChainNumberKey, String> modifiedNamesMap) {
        BasePairDTO newBasePair = new BasePairDTO(basePairDTO);
        newBasePair.nt1 = Residue.ofResidueWithNameFromMap(newBasePair.nt1, modifiedNamesMap);
        newBasePair.nt2 = Residue.ofResidueWithNameFromMap(newBasePair.nt2, modifiedNamesMap);
        return newBasePair;
    }

    public ImmutableBasePair toBasePair(PdbModel pdbModel) {
        PdbNamedResidueIdentifier left = mapResidueToPdbNamedResidueIdentifier(nt1,  pdbModel);
        PdbNamedResidueIdentifier right = mapResidueToPdbNamedResidueIdentifier(nt2,   pdbModel);
        return ImmutableBasePair.of(left, right);
    }

    private PdbNamedResidueIdentifier mapResidueToPdbNamedResidueIdentifier(Residue residue, PdbModel pdbModel) {
        if (pdbModel.hasResidue(residue)) {
        return pdbModel.findResidue(residue).namedResidueIdentifier();
        }

        String name = residue.getAuth().getName();
        return ImmutablePdbNamedResidueIdentifier.of(residue.chainIdentifier(), residue.residueNumber(), residue.insertionCode(), name.charAt(name.length() - 1));
    }

    public boolean isCanonical(PdbModel pdbModel) {
        if (saengerType == SaengerType.XIX || saengerType == SaengerType.XX || saengerType == SaengerType.XXVIII) {
            return true;
        }
        if (leontisWesthofType == LeontisWesthofType.CWW) {
            PdbNamedResidueIdentifier left = mapResidueToPdbNamedResidueIdentifier(nt1, pdbModel);
            PdbNamedResidueIdentifier right = mapResidueToPdbNamedResidueIdentifier(nt2, pdbModel);
            String sequence = Stream.of(left.oneLetterName(), right.oneLetterName())
                    .map(c -> Character.toString(c))
                    .map(String::toUpperCase)
                    .sorted().collect(Collectors.joining());
            return CANONICAL_ONE_LETTER_NAME_SORTED_PAIRS.contains(sequence);
        }
        return false;
    }
}

