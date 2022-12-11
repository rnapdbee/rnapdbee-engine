package pl.poznan.put.rnapdbee.engine.testhelp.tertiary;

public class TertiaryAnalysisOutputTestInformation {

    private final int bpSeqSize;
    private final int ctSize;
    private final int strandsSize;
    private final int structuralElementStemsSize;
    private final int structuralElementSLoopsSize;
    private final int structuralElementSingleStrandsSize;
    private final int structuralElementSingleStrands5pSize;
    private final int structuralElementSingleStrands3pSize;
    private final int coordinatesLineLength;
    private final int messagesSize;
    private final int canonicalInteractionsSize;
    private final int nonCanonicalInteractionsSize;
    private final int interStrandInteractionsSize;
    private final int stackingInteractionsSize;
    private final int basePhosphateInteractionsSize;
    private final int baseRiboseInteractionsSize;

    public int getBpSeqSize() {
        return bpSeqSize;
    }

    public int getCtSize() {
        return ctSize;
    }

    public int getStrandsSize() {
        return strandsSize;
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

    public int getCoordinatesLineLength() {
        return coordinatesLineLength;
    }

    public int getMessagesSize() {
        return messagesSize;
    }

    public int getCanonicalInteractionsSize() {
        return canonicalInteractionsSize;
    }

    public int getNonCanonicalInteractionsSize() {
        return nonCanonicalInteractionsSize;
    }

    public int getInterStrandInteractionsSize() {
        return interStrandInteractionsSize;
    }

    public int getStackingInteractionsSize() {
        return stackingInteractionsSize;
    }

    public int getBasePhosphateInteractionsSize() {
        return basePhosphateInteractionsSize;
    }

    public int getBaseRiboseInteractionsSize() {
        return baseRiboseInteractionsSize;
    }

    private TertiaryAnalysisOutputTestInformation(int bpSeqSize,
                                                  int ctSize,
                                                  int strandsSize,
                                                  int structuralElementStemsSize,
                                                  int structuralElementSLoopsSize,
                                                  int structuralElementSingleStrandsSize,
                                                  int structuralElementSingleStrands5pSize,
                                                  int structuralElementSingleStrands3pSize,
                                                  int coordinatesLineLength,
                                                  int messagesSize,
                                                  int canonicalInteractionsSize,
                                                  int nonCanonicalInteractionsSize,
                                                  int interStrandInteractionsSize,
                                                  int stackingInteractionsSize,
                                                  int basePhosphateInteractionsSize,
                                                  int baseRiboseInteractionsSize) {
        this.bpSeqSize = bpSeqSize;
        this.ctSize = ctSize;
        this.strandsSize = strandsSize;
        this.structuralElementStemsSize = structuralElementStemsSize;
        this.structuralElementSLoopsSize = structuralElementSLoopsSize;
        this.structuralElementSingleStrandsSize = structuralElementSingleStrandsSize;
        this.structuralElementSingleStrands5pSize = structuralElementSingleStrands5pSize;
        this.structuralElementSingleStrands3pSize = structuralElementSingleStrands3pSize;
        this.coordinatesLineLength = coordinatesLineLength;
        this.messagesSize = messagesSize;
        this.canonicalInteractionsSize = canonicalInteractionsSize;
        this.nonCanonicalInteractionsSize = nonCanonicalInteractionsSize;
        this.interStrandInteractionsSize = interStrandInteractionsSize;
        this.stackingInteractionsSize = stackingInteractionsSize;
        this.basePhosphateInteractionsSize = basePhosphateInteractionsSize;
        this.baseRiboseInteractionsSize = baseRiboseInteractionsSize;
    }

    public static class Builder {
        private int bpSeqSize;
        private int ctSize;
        private int strandsSize;
        private int structuralElementStemsSize;
        private int structuralElementSLoopsSize;
        private int structuralElementSingleStrandsSize;
        private int structuralElementSingleStrands5pSize;
        private int structuralElementSingleStrands3pSize;
        private int coordinatesLineLength;
        private int messagesSize;
        private int canonicalInteractionsSize;
        private int nonCanonicalInteractionsSize;
        private int interStrandInteractionsSize;
        private int stackingInteractionsSize;
        private int basePhosphateInteractionsSize;
        private int baseRiboseInteractionsSize;

        public Builder withBpSeqSize(int bpSeqSize) {
            this.bpSeqSize = bpSeqSize;
            return this;
        }

        public Builder withCtSize(int ctSize) {
            this.ctSize = ctSize;
            return this;
        }

        public Builder withStrandsSize(int strandsSize) {
            this.strandsSize = strandsSize;
            return this;
        }

        public Builder withStructuralElementStemsSize(int structuralElementStemsSize) {
            this.structuralElementStemsSize = structuralElementStemsSize;
            return this;
        }

        public Builder withStructuralElementSLoopsSize(int structuralElementSLoopsSize) {
            this.structuralElementSLoopsSize = structuralElementSLoopsSize;
            return this;
        }

        public Builder withStructuralElementSingleStrandsSize(int structuralElementSingleStrandsSize) {
            this.structuralElementSingleStrandsSize = structuralElementSingleStrandsSize;
            return this;
        }

        public Builder withStructuralElementSingleStrands5pSize(int structuralElementSingleStrands5pSize) {
            this.structuralElementSingleStrands5pSize = structuralElementSingleStrands5pSize;
            return this;
        }

        public Builder withStructuralElementSingleStrands3pSize(int structuralElementSingleStrands3pSize) {
            this.structuralElementSingleStrands3pSize = structuralElementSingleStrands3pSize;
            return this;
        }

        public Builder withCoordinatesLineLength(int coordinatesLineLength) {
            this.coordinatesLineLength = coordinatesLineLength;
            return this;
        }

        public Builder withMessagesSize(int messagesSize) {
            this.messagesSize = messagesSize;
            return this;
        }

        public Builder withCanonicalInteractionsSize(int canonicalInteractionsSize) {
            this.canonicalInteractionsSize = canonicalInteractionsSize;
            return this;
        }

        public Builder withNonCanonicalInteractionsSize(int nonCanonicalInteractionsSize) {
            this.nonCanonicalInteractionsSize = nonCanonicalInteractionsSize;
            return this;
        }

        public Builder withInterStrandInteractionsSize(int interStrandInteractionsSize) {
            this.interStrandInteractionsSize = interStrandInteractionsSize;
            return this;
        }

        public Builder withStackingInteractionsSize(int stackingInteractionsSize) {
            this.stackingInteractionsSize = stackingInteractionsSize;
            return this;
        }

        public Builder withBasePhosphateInteractionsSize(int basePhosphateInteractionsSize) {
            this.basePhosphateInteractionsSize = basePhosphateInteractionsSize;
            return this;
        }

        public Builder withBaseRiboseInteractionsSize(int baseRiboseInteractionsSize) {
            this.baseRiboseInteractionsSize = baseRiboseInteractionsSize;
            return this;
        }

        public TertiaryAnalysisOutputTestInformation build() {
            return new TertiaryAnalysisOutputTestInformation(
                    bpSeqSize,
                    ctSize,
                    strandsSize,
                    structuralElementStemsSize,
                    structuralElementSLoopsSize,
                    structuralElementSingleStrandsSize,
                    structuralElementSingleStrands5pSize,
                    structuralElementSingleStrands3pSize,
                    coordinatesLineLength,
                    messagesSize,
                    canonicalInteractionsSize,
                    nonCanonicalInteractionsSize,
                    interStrandInteractionsSize,
                    stackingInteractionsSize,
                    basePhosphateInteractionsSize,
                    baseRiboseInteractionsSize);
        }
    }
}
