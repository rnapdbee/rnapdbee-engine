package pl.poznan.put.rnapdbee.engine.shared.integration.adapters.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbResidue;

public class ResiduePayload {

    @JsonProperty
    private final String chain;

    @JsonProperty
    private final Integer number;

    @JsonProperty
    private final String name;

    public String getChain() {
        return chain;
    }

    public Integer getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public ResiduePayload(final String chain, final Integer number, final String name) {
        this.chain = chain;
        this.number = number;
        this.name = name;
    }

    public static ResiduePayload of(final PdbResidue another) {
        return new ResiduePayload(
                another.chainIdentifier(),
                another.residueNumber(),
                String.valueOf(another.oneLetterName()));
    }

    public static ResiduePayload of(final PdbNamedResidueIdentifier another) {
        return new ResiduePayload(
                another.chainIdentifier(),
                another.residueNumber(),
                String.valueOf(another.oneLetterName()));
    }
}
