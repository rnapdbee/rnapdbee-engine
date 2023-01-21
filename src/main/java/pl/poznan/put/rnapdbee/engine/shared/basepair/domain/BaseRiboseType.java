package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;


import com.fasterxml.jackson.annotation.JsonCreator;
import pl.poznan.put.notation.BR;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * enum for BaseRiboseType
 */
public enum BaseRiboseType {

    ZERO("0BR", BR._0),
    ONE("1BR", BR._1),
    TWO("2BR", BR._2),
    THREE("3BR", BR._3),
    FOUR("4BR", BR._4),
    FIVE("5BR", BR._5),
    SIX("6BR", BR._6),
    SEVEN("7BR", BR._7),
    EIGHT("8BR", BR._8),
    NINE("9BR", BR._9);

    public final String presentationValue;
    public final BR bioCommonsEnum;

    BaseRiboseType(String presentationValue, BR bioCommonsEnum) {
        this.presentationValue = presentationValue;
        this.bioCommonsEnum = bioCommonsEnum;
    }

    public static BR mapToBioCommonsForm(BaseRiboseType baseRiboseType) {
        if (baseRiboseType == null) {
            return BR.UNKNOWN;
        }
        return baseRiboseType.bioCommonsEnum;
    }

    public static BaseRiboseType mapFromBioCommonsForm(BR bPh) {
        if (bPh == BR.UNKNOWN || bPh == null) {
            return null;
        }
        return bioCommonsBphToBaseRiboseType
                .get(bPh);
    }

    @JsonCreator
    public static BaseRiboseType deserialize(String value) {
        if (value == null) {
            return null;
        }
        return serializedValueToBaseRiboseType
                .get(value);
    }

    private static final Map<BR, BaseRiboseType> bioCommonsBphToBaseRiboseType = new HashMap<>();
    private static final Map<String, BaseRiboseType> serializedValueToBaseRiboseType = new HashMap<>();

    static {
        Arrays.stream(BaseRiboseType
                        .values())
                .forEach(baseRiboseType -> {
                    bioCommonsBphToBaseRiboseType.put(baseRiboseType.bioCommonsEnum, baseRiboseType);
                    serializedValueToBaseRiboseType.put(baseRiboseType.presentationValue, baseRiboseType);
                });
    }
}
