package pl.poznan.put.rnapdbee.engine.calculation.testhelp;

public class AnalysisOutputTestInformation {

    private int bpSeqSize;
    private int ctEntriesSize;
    private int dotBracketLength;
    private int strandsSize;
    private int interactionsSize;
    private int structuralElementStemsSize;
    private int structuralElementSLoopsSize;
    private int structuralElementSingleStrandsSize;
    private int structuralElementSingleStrands5pSize;
    private int structuralElementSingleStrands3pSize;

    public int getBpSeqSize() {
        return bpSeqSize;
    }

    public int getCtEntriesSize() {
        return ctEntriesSize;
    }

    public int getDotBracketLength() {
        return dotBracketLength;
    }

    public int getStrandsSize() {
        return strandsSize;
    }

    public int getInteractionsSize() {
        return interactionsSize;
    }

    public int getStructuralElementStemsSize() {
        return structuralElementStemsSize;
    }

    public int getStructuralElementSLoopsSize() {
        return structuralElementSLoopsSize;
    }

    public int getStructuralElementSingleStrandsSize() {
        return structuralElementSingleStrandsSize;
    }

    public int getStructuralElementSingleStrands5pSize() {
        return structuralElementSingleStrands5pSize;
    }

    public int getStructuralElementSingleStrands3pSize() {
        return structuralElementSingleStrands3pSize;
    }

    public AnalysisOutputTestInformation withBpSeqSize(int bpSeqSize) {
        this.bpSeqSize = bpSeqSize;
        return this;
    }

    public AnalysisOutputTestInformation withCtEntriesSize(int ctEntriesSize) {
        this.ctEntriesSize = ctEntriesSize;
        return this;
    }

    public AnalysisOutputTestInformation withDotBracketLength(int dotBracketLength) {
        this.dotBracketLength = dotBracketLength;
        return this;
    }

    public AnalysisOutputTestInformation withStrandsSize(int strandsSize) {
        this.strandsSize = strandsSize;
        return this;
    }

    public AnalysisOutputTestInformation withInteractionsSize(int interactionsSize) {
        this.interactionsSize = interactionsSize;
        return this;
    }

    public AnalysisOutputTestInformation withStructuralElementStemsSize(
            int structuralElementStemsSize) {
        this.structuralElementStemsSize = structuralElementStemsSize;
        return this;
    }

    public AnalysisOutputTestInformation withStructuralElementSLoopsSize(
            int structuralElementSLoopsSize) {
        this.structuralElementSLoopsSize = structuralElementSLoopsSize;
        return this;
    }

    public AnalysisOutputTestInformation withStructuralElementSingleStrandsSize(
            int structuralElementSingleStrandsSize) {
        this.structuralElementSingleStrandsSize = structuralElementSingleStrandsSize;
        return this;
    }

    public AnalysisOutputTestInformation withStructuralElementSingleStrands5pSize(
            int structuralElementSingleStrands5pSize) {
        this.structuralElementSingleStrands5pSize = structuralElementSingleStrands5pSize;
        return this;
    }

    public AnalysisOutputTestInformation withStructuralElementSingleStrands3pSize(
            int structuralElementSingleStrands3pSize) {
        this.structuralElementSingleStrands3pSize = structuralElementSingleStrands3pSize;
        return this;
    }
}
