package pl.poznan.put.rnapdbee.engine.shared.integration.adapters.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.formats.DotBracket;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AdaptersVisualizationPayload {

    @JsonProperty
    private final List<StrandPayload> strands;

    @JsonProperty
    private final List<ResiduePayload> residues;

    @JsonProperty
    private final List<ChainWithResiduesPayload> chainsWithResidues;

    @JsonProperty
    private final NonCanonicalInteractionsPayload nonCanonicalInteractions;

    public List<StrandPayload> getStrands() {
        return strands;
    }

    public List<ResiduePayload> getResidues() {
        return residues;
    }

    public List<ChainWithResiduesPayload> getChainsWithResidues() {
        return chainsWithResidues;
    }

    public NonCanonicalInteractionsPayload getNonCanonicalInteractions() {
        return nonCanonicalInteractions;
    }

    public AdaptersVisualizationPayload(List<StrandPayload> strands,
                                        List<ResiduePayload> residues,
                                        List<ChainWithResiduesPayload> chainsWithResidues,
                                        NonCanonicalInteractionsPayload nonCanonicalInteractions) {
        this.strands = strands;
        this.residues = residues;
        this.chainsWithResidues = chainsWithResidues;
        this.nonCanonicalInteractions = nonCanonicalInteractions;
    }

    public static AdaptersVisualizationPayload of(DotBracket dotBracket,
                                                  PdbModel pdbModel,
                                                  List<? extends ClassifiedBasePair> nonCanonicalPairs) {

        List<StrandPayload> strands = dotBracket
                .strands().stream()
                .map(StrandPayload::ofStrand).collect(Collectors.toList());
        List<ResiduePayload> residues = pdbModel
                .residues().stream()
                .map(ResiduePayload::of).collect(Collectors.toList());
        List<ChainWithResiduesPayload> chainsWithResidues = pdbModel
                .chains().stream()
                .map(ChainWithResiduesPayload::of).collect(Collectors.toList());
        NonCanonicalInteractionsPayload nonCanonicalInteractionsPayload =
                NonCanonicalInteractionsPayload.of(nonCanonicalPairs);

        return new AdaptersVisualizationPayload(strands, residues, chainsWithResidues, nonCanonicalInteractionsPayload);
    }

    public static AdaptersVisualizationPayload of(DotBracket dotBracket) {

        List<StrandPayload> strands = dotBracket
                .strands().stream()
                .map(StrandPayload::ofStrand).collect(Collectors.toList());

        return new AdaptersVisualizationPayload(
                strands,
                Collections.emptyList(),
                Collections.emptyList(),
                NonCanonicalInteractionsPayload.EMPTY_PAYLOAD);
    }
}
