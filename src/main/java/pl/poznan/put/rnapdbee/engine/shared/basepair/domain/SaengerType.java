package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;

import pl.poznan.put.notation.Saenger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum SaengerType {
    I(Saenger.I),
    II(Saenger.II),
    III(Saenger.III),
    IV(Saenger.IV),
    V(Saenger.V),
    VI(Saenger.VI),
    VII(Saenger.VII),
    VIII(Saenger.VIII),
    IX(Saenger.IX),
    X(Saenger.X),
    XI(Saenger.XI),
    XII(Saenger.XII),
    XIII(Saenger.XIII),
    XIV(Saenger.XIV),
    XV(Saenger.XV),
    XVI(Saenger.XVI),
    XVII(Saenger.XVII),
    XVIII(Saenger.XVIII),
    XIX(Saenger.XIX),
    XX(Saenger.XX),
    XXI(Saenger.XXI),
    XXII(Saenger.XXII),
    XXIII(Saenger.XXIII),
    XXIV(Saenger.XXIV),
    XXV(Saenger.XXV),
    XXVI(Saenger.XXVI),
    XXVII(Saenger.XXVII),
    XXVIII(Saenger.XXVIII);

    public final Saenger bioCommonsEnum;

    SaengerType(Saenger bioCommonsEnum) {
        this.bioCommonsEnum = bioCommonsEnum;
    }

    public static Saenger mapToBioCommonsForm(SaengerType SaengerType) {
        if (SaengerType == null) {
            return Saenger.UNKNOWN;
        }
        return SaengerType.bioCommonsEnum;
    }

    public static SaengerType mapFromBioCommonsForm(Saenger bPh) {
        if (bPh == Saenger.UNKNOWN || bPh == null) {
            return null;
        }
        return bioCommonsSaengerToSaengerType.get(bPh);
    }

    private static final Map<Saenger, SaengerType> bioCommonsSaengerToSaengerType = new HashMap<>();

    static {
        Arrays.stream(SaengerType
                        .values())
                .forEach(baseRiboseType ->
                        bioCommonsSaengerToSaengerType.put(baseRiboseType.bioCommonsEnum, baseRiboseType));
    }
}
