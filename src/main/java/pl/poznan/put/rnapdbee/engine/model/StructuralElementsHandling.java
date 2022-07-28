package pl.poznan.put.rnapdbee.engine.model;


/**
 * enum for StructuralElementsHandling
 */
public enum StructuralElementsHandling {

    USE_PSEUDOKNOTS(true, true),
    IGNORE_PSEUDOKNOTS(false, false);

    private final boolean canElementsEndWithPseudoknots;
    private final boolean reuseSingleStrandsFromLoops;

    StructuralElementsHandling(boolean canElementsEndWithPseudoknots,
                               boolean reuseSingleStrandsFromLoops) {
        this.canElementsEndWithPseudoknots = canElementsEndWithPseudoknots;
        this.reuseSingleStrandsFromLoops = reuseSingleStrandsFromLoops;
    }

    public boolean canElementsEndWithPseudoknots() {
        return canElementsEndWithPseudoknots;
    }

    public boolean isReuseSingleStrandsFromLoopsEnabled() {
        return reuseSingleStrandsFromLoops;
    }
}
