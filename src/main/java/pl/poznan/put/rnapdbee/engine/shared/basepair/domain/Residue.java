package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.ChainNumberKey;

import java.util.Map;


/**
 * DTO class for Residue
 */
public class Residue {

    @JsonProperty("label")
    private ResidueLabel label;

    @JsonProperty("auth")
    private ResidueAuth auth;

    public ResidueLabel getLabel() {
        return label;
    }

    public ResidueAuth getAuth() {
        return auth;
    }

    public void setLabel(ResidueLabel label) {
        this.label = label;
    }

    public void setAuth(ResidueAuth auth) {
        this.auth = auth;
    }

    public Residue() {
    }

    private Residue(Residue residue) {
        this.label = residue.getLabel();
        this.auth = residue.getAuth();
    }

    public static Residue ofResidueWithNameFromMap(Residue residue,
                                                   Map<ChainNumberKey, String> modifiedNamesMap) {
        Residue newResidue = new Residue(residue);
        if (newResidue.label != null) {
            ChainNumberKey key = new ChainNumberKey(newResidue.label.getChainIdentifier(),
                    newResidue.label.getResidueNumber(), null);
            if (modifiedNamesMap.containsKey(key)) {
                newResidue.label = ResidueLabel
                        .ofResidueLabelWithExchangedName(newResidue.label, modifiedNamesMap.get(key));
            }
        }
        if (newResidue.auth != null) {
            ChainNumberKey key = new ChainNumberKey(newResidue.auth.getChainIdentifier(),
                    newResidue.auth.getResidueNumber(), residue.auth.getInsertionCode().orElse(null));
            if (modifiedNamesMap.containsKey(key)) {
                newResidue.auth = ResidueAuth
                        .ofResidueAuthWithExchangedName(newResidue.auth, modifiedNamesMap.get(key));
            }
        }
        return newResidue;
    }

}
