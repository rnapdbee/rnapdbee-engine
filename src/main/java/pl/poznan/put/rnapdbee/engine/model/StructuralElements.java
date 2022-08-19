package pl.poznan.put.rnapdbee.engine.model;

/**
 * DTO enum for Structural Elements - implementation taken from rnapdbee-common StructuralElementsIdentificationModeEnum
 */
public enum StructuralElements {

    // TODO: is it the same as StructuralElementsHandling?
    //  if yes - probably could be deleted
    PSEUDOKNOTS_AS_PAIRED_RESIDUES(
            "Structural elements identified treating pseudoknots as paired residues",
            "pseudoknots_as_paired_residues",
            true,
            true),
    PSEUDOKNOTS_AS_UNPAIRED_RESIDUES(
            "Structural elements identified treating pseudoknots as unpaired residues",
            "pseudoknots_as_unpaired_residues",
            false,
            false);

    private final String displayName;
    private final String archiveName;
    private final boolean canElementsEndWithPseudoknots;
    private final boolean reuseSingleStrandsFromLoops;

    StructuralElements(
            final String displayName,
            final boolean canElementsEndWithPseudoknots,
            final boolean reuseSingleStrandsFromLoops) {
        this(displayName, displayName, canElementsEndWithPseudoknots, reuseSingleStrandsFromLoops);
    }

    StructuralElements(
            final String displayName,
            final String archiveName,
            final boolean canElementsEndWithPseudoknots,
            final boolean reuseSingleStrandsFromLoops) {
        this.displayName = displayName;
        this.archiveName = archiveName;
        this.canElementsEndWithPseudoknots = canElementsEndWithPseudoknots;
        this.reuseSingleStrandsFromLoops = reuseSingleStrandsFromLoops;
    }

    public boolean isCanElementsEndWithPseudoknots() {
        return canElementsEndWithPseudoknots;
    }

    public boolean isReuseSingleStrandsFromLoops() {
        return reuseSingleStrandsFromLoops;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getArchiveName() {
        return archiveName;
    }
}
