package pl.poznan.put.rnapdbee.engine.basepair.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * enum for BR
 */
public enum BR {
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
    public static pl.poznan.put.notation.BR mapToBioCommonsBph(BR bphFromEngineModel) {
        switch (bphFromEngineModel) {
            case ZERO:
                return pl.poznan.put.notation.BR._0;
            case ONE:
                return pl.poznan.put.notation.BR._1;
            case TWO:
                return pl.poznan.put.notation.BR._2;
            case THREE:
                return pl.poznan.put.notation.BR._3;
            case FOUR:
                return pl.poznan.put.notation.BR._4;
            case FIVE:
                return pl.poznan.put.notation.BR._5;
            case SIX:
                return pl.poznan.put.notation.BR._6;
            case SEVEN:
                return pl.poznan.put.notation.BR._7;
            case EIGHT:
                return pl.poznan.put.notation.BR._8;
            case NINE:
                return pl.poznan.put.notation.BR._9;
            default:
                return pl.poznan.put.notation.BR.UNKNOWN;
        }
    }
}
