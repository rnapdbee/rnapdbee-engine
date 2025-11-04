package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for Residue Label
 */
public class ResidueLabel {

    @JsonProperty("chain")
    private String chainIdentifier;

    @JsonProperty("number")
    private Integer residueNumber;

    @JsonProperty("name")
    private String name;

    public ResidueLabel() {
    }

    private ResidueLabel(ResidueLabel residueLabel) {
        this.chainIdentifier = residueLabel.getChainIdentifier();
        this.residueNumber = residueLabel.getResidueNumber();
        this.name = residueLabel.getName();
    }

    public String getChainIdentifier() {
        return chainIdentifier;
    }

    public void setChainIdentifier(String chainIdentifier) {
        this.chainIdentifier = chainIdentifier;
    }

    public Integer getResidueNumber() {
        return residueNumber;
    }

    public void setResidueNumber(Integer residueNumber) {
        this.residueNumber = residueNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ResidueLabel ofResidueLabelWithExchangedName(ResidueLabel residueLabel,
                                                               String name) {
        ResidueLabel newResidueLabel = new ResidueLabel(residueLabel);
        if (!name.equals(residueLabel.name)) {
            newResidueLabel.name = name.toLowerCase();
        }
        return newResidueLabel;
    }

    @Override
    public String toString() {
        return "ResidueLabel{" +
                "chainIdentifier='" + chainIdentifier + '\'' +
                ", residueNumber=" + residueNumber +
                ", name='" + name + '\'' +
                '}';
    }
}
