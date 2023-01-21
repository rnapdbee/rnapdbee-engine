package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;


import com.fasterxml.jackson.annotation.JsonCreator;
import pl.poznan.put.notation.BPh;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * enum for possible BPh values handled by rnapdbee-engine
 */
public enum BasePhosphateType {

    ZERO("0BPh", BPh._0),
    ONE("1BPh", BPh._1),
    TWO("2BPh", BPh._2),
    THREE("3BPh", BPh._3),
    FOUR("4BPh", BPh._4),
    FIVE("5BPh", BPh._5),
    SIX("6BPh", BPh._6),
    SEVEN("7BPh", BPh._7),
    EIGHT("8BPh", BPh._8),
    NINE("9BPh", BPh._9);

    public final String presentationValue;
    public final BPh bioCommonsEnum;

    BasePhosphateType(String presentationValue, BPh bioCommonsEnum) {
        this.presentationValue = presentationValue;
        this.bioCommonsEnum = bioCommonsEnum;
    }

    public static BPh mapToBioCommonsForm(BasePhosphateType basePhosphateType) {
        if (basePhosphateType == null) {
            return BPh.UNKNOWN;
        }
        return basePhosphateType.bioCommonsEnum;
    }

    public static BasePhosphateType mapFromBioCommonsForm(BPh bPh) {
        if (bPh == BPh.UNKNOWN || bPh == null) {
            return null;
        }
        return bioCommonsBphToBasePhosphateType.get(bPh);
    }

    @JsonCreator
    public static BasePhosphateType deserialize(String value) {
        if (value == null) {
            return null;
        }
        return serializedValueToBasePhosphateType.get(value);
    }

    private static final Map<BPh, BasePhosphateType> bioCommonsBphToBasePhosphateType = new HashMap<>();
    private static final Map<String, BasePhosphateType> serializedValueToBasePhosphateType = new HashMap<>();

    static {
        Arrays.stream(BasePhosphateType.values())
                .forEach(basePhosphateType -> {
                    bioCommonsBphToBasePhosphateType.put(basePhosphateType.bioCommonsEnum, basePhosphateType);
                    serializedValueToBasePhosphateType.put(basePhosphateType.presentationValue, basePhosphateType);
                });
    }
}
