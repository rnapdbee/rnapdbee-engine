package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * enum for BR
 */
public enum BR {
    @JsonProperty("0BR")
    ZERO,
    @JsonProperty("1BR")
    ONE,
    @JsonProperty("2BR")
    TWO,
    @JsonProperty("3BR")
    THREE,
    @JsonProperty("4BR")
    FOUR,
    @JsonProperty("5BR")
    FIVE,
    @JsonProperty("6BR")
    SIX,
    @JsonProperty("7BR")
    SEVEN,
    @JsonProperty("8BR")
    EIGHT,
    @JsonProperty("9BR")
    NINE;

    public static pl.poznan.put.notation.BR mapToBioCommonsBr(BR brFromEngineModel) {
        if (brFromEngineModel == null) {
            return pl.poznan.put.notation.BR.UNKNOWN;
        }
        switch (brFromEngineModel) {
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

    public static BR mapBioCommonsBrToEngineBr(pl.poznan.put.notation.BR brFromEngineModel) {
        switch (brFromEngineModel) {
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
