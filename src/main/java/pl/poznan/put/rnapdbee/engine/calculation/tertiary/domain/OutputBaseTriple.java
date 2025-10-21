package pl.poznan.put.rnapdbee.engine.calculation.tertiary.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.*;
import pl.poznan.put.rnapdbee.engine.shared.multiplet.BaseTriple;
import pl.poznan.put.structure.ClassifiedBasePair;

import java.util.Optional;

/**
 * Class representing base triple returned to the REST consumer.
 */
public class OutputBaseTriple {
    @JsonProperty("residue")
    OutputNamedResidue residue;

    @JsonProperty("type")
    String type;

    @JsonProperty("firstPartner")
    OutputNamedResidue firstPartner;

    @JsonProperty("secondPartner")
    OutputNamedResidue secondPartner;

    public static OutputBaseTriple fromBaseTriple(BaseTriple baseTriple) {
        OutputBaseTriple outputBaseTriple = new OutputBaseTriple();
        outputBaseTriple.setResidue(OutputNamedResidue.fromPdbNamedResidueIdentifier(baseTriple.getIdentifier()));
        outputBaseTriple.setType(baseTriple.type());
        outputBaseTriple.setFirstPartner(OutputNamedResidue.fromPdbNamedResidueIdentifier(
                baseTriple.getFirstBasePair().basePair().right()));
        outputBaseTriple.setSecondPartner(OutputNamedResidue.fromPdbNamedResidueIdentifier(
                baseTriple.getSecondBasePair().basePair().right()));
        return outputBaseTriple;
    }

    public OutputNamedResidue getResidue() {
        return residue;
    }

    public void setResidue(OutputNamedResidue residue) {
        this.residue = residue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OutputNamedResidue getFirstPartner() {
        return firstPartner;
    }

    public void setFirstPartner(OutputNamedResidue firstPartner) {
        this.firstPartner = firstPartner;
    }

    public OutputNamedResidue getSecondPartner() {
        return secondPartner;
    }

    public void setSecondPartner(OutputNamedResidue secondPartner) {
        this.secondPartner = secondPartner;
    }
}
