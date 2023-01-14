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

    public static ResidueLabel ofResidueLabelWithExchangedName(ResidueLabel residueLabel,
                                                               String name) {
        ResidueLabel newResidueLabel = new ResidueLabel(residueLabel);
        if (!name.equals(residueLabel.name)) {
            newResidueLabel.name = name.toLowerCase();
        }
        return newResidueLabel;
    }

    public String getChainIdentifier() {
        return chainIdentifier;
    }

    public Integer getResidueNumber() {
        return residueNumber;
    }

    public String getName() {
        return name;
    }
}
