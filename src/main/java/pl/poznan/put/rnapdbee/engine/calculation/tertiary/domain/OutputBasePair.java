package pl.poznan.put.rnapdbee.engine.calculation.tertiary.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePhosphateType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BaseRiboseType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.LeontisWesthofType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.SaengerType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.StackingTopology;
import pl.poznan.put.structure.ClassifiedBasePair;

import java.util.Optional;


/**
 * Class representing base pair returned to the REST consumer.
 */
public class OutputBasePair {

    @JsonProperty("leftResidue")
    OutputNamedResidue leftResidue;
    @JsonProperty("rightResidue")
    OutputNamedResidue rightResidue;
    @JsonProperty("interactionType")
    String interactionType;
    @JsonProperty("saenger")
    String saenger;
    @JsonProperty("leontisWesthof")
    String leontisWesthof;
    @JsonProperty("bPh")
    String bPh;
    @JsonProperty("br")
    String br;
    @JsonProperty("stackingTopology")
    StackingTopology stackingTopology;

    public static OutputBasePair fromClassifiedBasePair(ClassifiedBasePair classifiedBasePair) {
        OutputBasePair outputBasePair = new OutputBasePair();

        outputBasePair.setInteractionType(classifiedBasePair.interactionType().left().name() + " - " + classifiedBasePair.interactionType().right().name());
        outputBasePair.setSaenger(
                Optional.ofNullable(SaengerType.mapFromBioCommonsForm(classifiedBasePair.saenger()))
                        .map(Enum::toString)
                        .orElse(null));
        outputBasePair.setLeontisWesthof(
                Optional.ofNullable(LeontisWesthofType.mapFromBioCommonsForm(classifiedBasePair.leontisWesthof()))
                        .map(lw -> lw.presentationValue)
                        .orElse(null));
        outputBasePair.setBPh(
                Optional.ofNullable(BasePhosphateType.mapFromBioCommonsForm(classifiedBasePair.bph()))
                        .map(bph -> bph.presentationValue)
                        .orElse(null));
        outputBasePair.setBr(
                Optional.ofNullable(BaseRiboseType.mapFromBioCommonsForm(classifiedBasePair.br()))
                        .map(br -> br.presentationValue)
                        .orElse(null));
        outputBasePair.setStackingTopology(StackingTopology.mapFromBioCommonsForm(classifiedBasePair
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

    public String getSaenger() {
        return saenger;
    }

    public void setSaenger(String saenger) {
        this.saenger = saenger;
    }

    public String getLeontisWesthof() {
        return leontisWesthof;
    }

    public void setLeontisWesthof(String leontisWesthof) {
        this.leontisWesthof = leontisWesthof;
    }

    public String getBPh() {
        return bPh;
    }

    public void setBPh(String basePhosphateType) {
        this.bPh = basePhosphateType;
    }

    public String getBr() {
        return br;
    }

    public void setBr(String baseRiboseType) {
        this.br = baseRiboseType;
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
