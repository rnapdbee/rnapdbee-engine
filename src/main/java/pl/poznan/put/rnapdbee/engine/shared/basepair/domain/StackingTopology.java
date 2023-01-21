package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * enum for Stacking Topology
 */
public enum StackingTopology {

    @JsonProperty("upward")
    UPWARD,
    @JsonProperty("downward")
    DOWNWARD,
    @JsonProperty("inward")
    INWARD,
    @JsonProperty("outward")
    OUTWARD;

    public static pl.poznan.put.notation.StackingTopology mapToBioCommonsForm(StackingTopology stackingTopology) {
        if (stackingTopology == null) {
            return pl.poznan.put.notation.StackingTopology.UNKNOWN;
        }
        return pl.poznan.put.notation.StackingTopology.valueOf(stackingTopology.toString());
    }

    public static StackingTopology mapFromBioCommonsForm(pl.poznan.put.notation.StackingTopology stackingTopology) {
        if (stackingTopology == pl.poznan.put.notation.StackingTopology.UNKNOWN || stackingTopology == null) {
            return null;
        }
        return StackingTopology.valueOf(stackingTopology.toString());
    }
}
