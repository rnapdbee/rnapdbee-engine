package pl.poznan.put.rnapdbee.engine.calculation.tertiary.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePhosphateType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BR;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.StackingTopology;
import pl.poznan.put.structure.ClassifiedBasePair;

import java.util.Optional;

import static pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePhosphateType.mapFromBioCommonsForm;


/**
 * Class representing base pair returned to the REST consumer.
 */
public class OutputBasePair {

    @JsonProperty("interactionType")
    String interactionType;
    @JsonProperty("saenger")
    Saenger saenger;
    @JsonProperty("leontisWesthof")
    LeontisWesthof leontisWesthof;
    @JsonProperty("bPh")
    String basePhosphateType;
    @JsonProperty("br")
    BR br;
    @JsonProperty("stackingTopology")
    StackingTopology stackingTopology;
    @JsonProperty("leftResidue")
    OutputNamedResidue leftResidue;
    @JsonProperty("rightResidue")
    OutputNamedResidue rightResidue;

    public static OutputBasePair fromClassifiedBasePair(ClassifiedBasePair classifiedBasePair) {
        OutputBasePair outputBasePair = new OutputBasePair();

        outputBasePair.setInteractionType(classifiedBasePair.interactionType().left().name() + " - " + classifiedBasePair.interactionType().right().name());
        outputBasePair.setSaenger(classifiedBasePair.saenger());
        outputBasePair.setLeontisWesthof(classifiedBasePair.leontisWesthof());
        outputBasePair.setbPh(Optional.ofNullable(mapFromBioCommonsForm(classifiedBasePair.bph()))
                .map(bph -> bph.presentationValue)
                .orElse(null));
        outputBasePair.setBr(BR.mapBioCommonsBrToEngineBr(classifiedBasePair.br()));
        outputBasePair.setStackingTopology(StackingTopology.convertFromBioCommonsEntity(classifiedBasePair
                .stackingTopology()));

        OutputNamedResidue leftResidue = OutputNamedResidue.fromPdbNamedResidueIdentifier(
                classifiedBasePair.basePair().left());
        OutputNamedResidue rightResidue = OutputNamedResidue.fromPdbNamedResidueIdentifier(
                classifiedBasePair.basePair().right());

        outputBasePair.setLeftResidue(leftResidue);
        outputBasePair.setRightResidue(rightResidue);

        return outputBasePair;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }

    public Saenger getSaenger() {
        return saenger;
    }

    public void setSaenger(Saenger saenger) {
        this.saenger = saenger;
    }

    public LeontisWesthof getLeontisWesthof() {
        return leontisWesthof;
    }

    public void setLeontisWesthof(LeontisWesthof leontisWesthof) {
        this.leontisWesthof = leontisWesthof;
    }

    public String getbPh() {
        return basePhosphateType;
    }

    public void setbPh(String basePhosphateType) {
        this.basePhosphateType = basePhosphateType;
    }

    public BR getBr() {
        return br;
    }

    public void setBr(BR br) {
        this.br = br;
    }

    public StackingTopology getStackingTopology() {
        return stackingTopology;
    }

    public void setStackingTopology(StackingTopology stackingTopology) {
        this.stackingTopology = stackingTopology;
    }

    public OutputNamedResidue getLeftResidue() {
        return leftResidue;
    }

    public void setLeftResidue(OutputNamedResidue leftResidue) {
        this.leftResidue = leftResidue;
    }

    public OutputNamedResidue getRightResidue() {
        return rightResidue;
    }

    public void setRightResidue(OutputNamedResidue rightResidue) {
        this.rightResidue = rightResidue;
    }
}
