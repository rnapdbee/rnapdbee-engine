package pl.poznan.put.rnapdbee.engine.shared.integration.adapters.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.structure.ClassifiedBasePair;

public class PairPayload {

    @JsonProperty
    private final ResiduePayload residueLeft;

    @JsonProperty
    private final ResiduePayload residueRight;

    @JsonProperty
    private final String leontisWesthof;

    public static PairPayload of(ClassifiedBasePair another) {
        ResiduePayload residueLeft = ResiduePayload.of(another.basePair().left());
        ResiduePayload residueRight = ResiduePayload.of(another.basePair().right());

        return new PairPayload(residueLeft, residueRight, another.leontisWesthof().fullName());
    }

    private PairPayload(ResiduePayload residueLeft, ResiduePayload residueRight, String leontisWesthof) {
        this.residueLeft = residueLeft;
        this.residueRight = residueRight;
        if (leontisWesthof.equals("n/a")) {
            this.leontisWesthof = null;
        } else {
            this.leontisWesthof = leontisWesthof;
        }
    }
}
