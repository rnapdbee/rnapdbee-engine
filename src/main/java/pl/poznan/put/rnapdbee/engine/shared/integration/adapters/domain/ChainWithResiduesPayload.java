package pl.poznan.put.rnapdbee.engine.shared.integration.adapters.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.pdb.analysis.PdbChain;

import java.util.List;
import java.util.stream.Collectors;

public class ChainWithResiduesPayload {

    @JsonProperty
    private final String name;

    @JsonProperty
    private final List<ResiduePayload> residues;

    public String getName() {
        return name;
    }

    public List<ResiduePayload> getResidues() {
        return residues;
    }

    private ChainWithResiduesPayload(String name, List<ResiduePayload> residues) {
        this.name = name;
        this.residues = residues;
    }

    public static ChainWithResiduesPayload of(PdbChain another) {
        List<ResiduePayload> residuePayloadList = another.residues()
                .stream().map(ResiduePayload::of)
                .collect(Collectors.toList());

        return new ChainWithResiduesPayload(another.identifier(), residuePayloadList);
    }
}
