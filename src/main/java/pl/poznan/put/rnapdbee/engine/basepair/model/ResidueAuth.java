package pl.poznan.put.rnapdbee.engine.basepair.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;

/**
 * DTO class for ResidueAuth
 */
public class ResidueAuth extends PdbNamedResidueIdentifier {

    @JsonProperty("chain")
    private String chainIdentifier;

    @JsonProperty("number")
    private Integer residueNumber;

    @JsonProperty("icode")
    private String insertionCode;

    @JsonProperty("name")
    private String name;

    public String getChainIdentifier() {
        return chainIdentifier;
    }

    public Integer getResidueNumber() {
        return residueNumber;
    }

    public String getInsertionCode() {
        return insertionCode;
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
        return insertionCode;
    }

    @Override
    public char oneLetterName() {
        return name.charAt(0);
    }
}
