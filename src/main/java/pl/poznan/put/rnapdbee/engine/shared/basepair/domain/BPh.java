package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * enum for BPh
 */
public enum BPh {

    @JsonProperty("0BPh")
    ZERO,
    @JsonProperty("1BPh")
    ONE,
    @JsonProperty("2BPh")
    TWO,
    @JsonProperty("3BPh")
    THREE,
    @JsonProperty("4BPh")
    FOUR,
    @JsonProperty("5BPh")
    FIVE,
    @JsonProperty("6BPh")
    SIX,
    @JsonProperty("7BPh")
    SEVEN,
    @JsonProperty("8BPh")
    EIGHT,
    @JsonProperty("9BPh")
    NINE;

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

    public static BPh mapBioCommonsBphToEngineBph(pl.poznan.put.notation.BPh bphFromEngineModel) {
        switch (bphFromEngineModel) {
            case _0:
                return ZERO;
            case _1:
                return ONE;
            case _2:
                return TWO;
            case _3:
                return THREE;
            case _4:
                return FOUR;
            case _5:
                return FIVE;
            case _6:
                return SIX;
            case _7:
                return SEVEN;
            case _8:
                return EIGHT;
            case _9:
                return NINE;
            default:
                return null;
        }
    }
}
