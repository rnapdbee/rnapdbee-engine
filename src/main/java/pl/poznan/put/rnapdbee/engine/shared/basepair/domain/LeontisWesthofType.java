package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import pl.poznan.put.notation.LeontisWesthof;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum LeontisWesthofType {

    CWW("cWW", LeontisWesthof.CWW),
    CWH("cWH", LeontisWesthof.CWH),
    CWS("cWS", LeontisWesthof.CWS),
    CHW("cHW", LeontisWesthof.CHW),
    CHH("cHH", LeontisWesthof.CHH),
    CHS("cHS", LeontisWesthof.CHS),
    CSW("cSW", LeontisWesthof.CSW),
    CSH("cSH", LeontisWesthof.CSH),
    CSS("cSS", LeontisWesthof.CSS),
    TWW("tWW", LeontisWesthof.TWW),
    TWH("tWH", LeontisWesthof.TWH),
    TWS("tWS", LeontisWesthof.TWS),
    THW("tHW", LeontisWesthof.THW),
    THH("tHH", LeontisWesthof.THH),
    THS("tHS", LeontisWesthof.THS),
    TSW("tSW", LeontisWesthof.TSW),
    TSH("tSH", LeontisWesthof.TSH),
    TSS("tSS", LeontisWesthof.TSS);

    public final String presentationValue;
    public final LeontisWesthof bioCommonsEnum;

    LeontisWesthofType(String presentationValue, LeontisWesthof bioCommonsEnum) {
        this.presentationValue = presentationValue;
        this.bioCommonsEnum = bioCommonsEnum;
    }

    public static LeontisWesthof mapToBioCommonsForm(LeontisWesthofType leontisWesthofType) {
        if (leontisWesthofType == null) {
            return LeontisWesthof.UNKNOWN;
        }
        return leontisWesthofType.bioCommonsEnum;
    }

    public static LeontisWesthofType mapFromBioCommonsForm(LeontisWesthof bPh) {
        if (bPh == LeontisWesthof.UNKNOWN || bPh == null) {
            return null;
        }
        return bioCommonsBphToLeontisWesthofType
                .get(bPh);
    }

    @JsonCreator
    public static LeontisWesthofType deserialize(String value) {
        if (value == null) {
            return null;
        }
        return serializedValueToLeontisWesthofType
                .get(value);
    }

    private static final Map<LeontisWesthof, LeontisWesthofType> bioCommonsBphToLeontisWesthofType = new HashMap<>();
    private static final Map<String, LeontisWesthofType> serializedValueToLeontisWesthofType = new HashMap<>();

    static {
        Arrays.stream(LeontisWesthofType
                        .values())
                .forEach(leontisWesthofType -> {
                    bioCommonsBphToLeontisWesthofType.put(leontisWesthofType.bioCommonsEnum, leontisWesthofType);
                    serializedValueToLeontisWesthofType.put(leontisWesthofType.presentationValue, leontisWesthofType);
                });
    }
}
