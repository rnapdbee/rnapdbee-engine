package pl.poznan.put.rnapdbee.engine.basepair.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;

/**
 * DTO class for Residue Label
 */
public class ResidueLabel extends PdbNamedResidueIdentifier {

    @JsonProperty("chain")
    private String chainIdentifier;

    @JsonProperty("number")
    private Integer residueNumber;

    @JsonProperty("name")
    private String name;

    public String getChainIdentifier() {
        return chainIdentifier;
    }

    public Integer getResidueNumber() {
        return residueNumber;
    }

    public String getName() {
        return name;
    }

    @Override
    public String chainIdentifier() {
        return chainIdentifier;
    }

    @Override
    public int residueNumber() {
        return residueNumber;
    }

    @Override
    public String insertionCode() {
        return null;
    }

    @Override
    public char oneLetterName() {
        return name.charAt(0);
    }
}
