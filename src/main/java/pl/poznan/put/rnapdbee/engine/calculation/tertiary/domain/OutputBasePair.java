package pl.poznan.put.rnapdbee.engine.calculation.tertiary.domain;

import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BPh;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BR;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.StackingTopology;
import pl.poznan.put.structure.ClassifiedBasePair;


/**
 * Class representing base pair returned to the REST consumer.
 */
public class OutputBasePair {

    String interactionType;
    Saenger saenger;
    LeontisWesthof leontisWesthof;
    BPh bPh;
    BR br;
    StackingTopology stackingTopology;
    OutputNamedResidue leftResidue;
    OutputNamedResidue rightResidue;

    public static OutputBasePair fromClassifiedBasePair(ClassifiedBasePair classifiedBasePair) {
        OutputBasePair outputBasePair = new OutputBasePair();

        outputBasePair.setInteractionType(classifiedBasePair.interactionType().description());
        outputBasePair.setSaenger(classifiedBasePair.saenger());
        outputBasePair.setLeontisWesthof(classifiedBasePair.leontisWesthof());
        outputBasePair.setbPh(BPh.mapBioCommonsBphToEngineBph(classifiedBasePair.bph()));
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

    public BPh getbPh() {
        return bPh;
    }

    public void setbPh(BPh bPh) {
        this.bPh = bPh;
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
