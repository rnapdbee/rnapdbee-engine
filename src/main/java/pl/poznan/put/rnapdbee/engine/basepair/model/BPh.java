package pl.poznan.put.rnapdbee.engine.basepair.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * enum for BPh
 */
public enum BPh {

    @JsonProperty("0")
    ZERO,
    @JsonProperty("1")
    ONE,
    @JsonProperty("2")
    TWO,
    @JsonProperty("3")
    THREE,
    @JsonProperty("4")
    FOUR,
    @JsonProperty("5")
    FIVE,
    @JsonProperty("6")
    SIX,
    @JsonProperty("7")
    SEVEN,
    @JsonProperty("8")
    EIGHT,
    @JsonProperty("9")
    NINE;

    // TODO: get rid of this when merging rnapdbee-common code to engine
    public static pl.poznan.put.notation.BPh mapToBioCommonsBph(BPh bphFromEngineModel) {
        if (bphFromEngineModel == null) {
            return pl.poznan.put.notation.BPh.UNKNOWN;
        }
        switch (bphFromEngineModel) {
            case ZERO:
                return pl.poznan.put.notation.BPh._0;
            case ONE:
                return pl.poznan.put.notation.BPh._1;
            case TWO:
                return pl.poznan.put.notation.BPh._2;
            case THREE:
                return pl.poznan.put.notation.BPh._3;
            case FOUR:
                return pl.poznan.put.notation.BPh._4;
            case FIVE:
                return pl.poznan.put.notation.BPh._5;
            case SIX:
                return pl.poznan.put.notation.BPh._6;
            case SEVEN:
                return pl.poznan.put.notation.BPh._7;
            case EIGHT:
                return pl.poznan.put.notation.BPh._8;
            case NINE:
                return pl.poznan.put.notation.BPh._9;
            default:
                return pl.poznan.put.notation.BPh.UNKNOWN;
        }
    }
}
