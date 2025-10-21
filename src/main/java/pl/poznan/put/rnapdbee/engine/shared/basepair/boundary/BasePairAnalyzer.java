package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.notation.StackingTopology;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rna.InteractionType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.AdaptersAnalysisDTO;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairDTO;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePhosphateType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BaseRiboseType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.LeontisWesthofType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.SaengerType;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.basepair.exception.AdaptersErrorException;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RnaPDBeeAdaptersCaller;
import pl.poznan.put.rnapdbee.engine.shared.multiplet.MultipletSet;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.ImmutableAnalyzedBasePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.poznan.put.rnapdbee.engine.shared.basepair.domain.StackingTopology.mapToBioCommonsForm;

// TODO: WebFlux would be really efficient with the 3D->multi 2D analysis as we there perform multiple calls to the
//  adapters, it could be done in parallel and then joined up after each call is performed. We would save a tone of
//  time with this approach if the adapters were scalable horizontally in the future.
public abstract class BasePairAnalyzer {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final RnaPDBeeAdaptersCaller rnapdbeeAdaptersCaller;

    public abstract AnalysisTool analysisTool();

    // TODO: think about using WebFlux advancements when refactoring
    public abstract BasePairAnalysis analyze(String fileContent, boolean includeNonCanonical, PdbModel structureModel)
            throws AdaptersErrorException;

    protected BasePairAnalysis performAnalysis(String fileContent, boolean includeNonCanonical, PdbModel structureModel)
            throws AdaptersErrorException {
        LOGGER.info(String.format("base pair analysis started for model number %s", structureModel.modelNumber()));
        AdaptersAnalysisDTO adaptersAnalysis = rnapdbeeAdaptersCaller.performBasePairAnalysis(
                fileContent, analysisTool(), structureModel.modelNumber());
        LOGGER.info(String.format("base pair analysis finished for model number %s", structureModel.modelNumber()));
        return performPostAnalysisOnResponseFromAdapter(adaptersAnalysis, includeNonCanonical, structureModel);
    }

    /**
     * performs post analysis on response from adapter.
     * Separates response into canonical, nonCanonical, stackings, basePhosphate, baseRibose, otherInteractions,
     * and interStrand lists of base pairs.
     * <p>
     * Calculates represented list of base pairs.
     * Calculates messages list.
     *
     * @param responseFromAdapter response from the adapter
     * @param includeNonCanonical flag specifying if nonCanonical pairs should be included or not
     * @return {@link AnalyzedBasePair} BasePairAnalysis object with populated pairs lists.
     */
    protected BasePairAnalysis performPostAnalysisOnResponseFromAdapter(
            AdaptersAnalysisDTO responseFromAdapter, boolean includeNonCanonical, PdbModel structureModel) {
        final Map<ChainNumberKey, String> pairIdentifiersWithTheirShortNames = structureModel.residues().stream()
                .collect(Collectors.toMap(
                        residue -> new ChainNumberKey(
                                residue.chainIdentifier(),
                                residue.residueNumber(),
                                residue.insertionCode().orElse(null)),
                        residue -> String.valueOf(residue.oneLetterName())));

        List<AnalyzedBasePair> canonical = responseFromAdapter.getBasePairs().stream()
                .filter(BasePairDTO::isCanonical)
                .map(pair -> BasePairDTO.ofBasePairDTOWithNameFromMap(pair, pairIdentifiersWithTheirShortNames))
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair)
                        .withInteractionType(InteractionType.BASE_BASE)
                        .withSaenger(SaengerType.mapToBioCommonsForm(basePair.getSaengerType()))
                        .withLeontisWesthof(LeontisWesthofType.mapToBioCommonsForm(basePair.getLeontisWesthofType()))
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)
                        .withStackingTopology(StackingTopology.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> nonCanonical = responseFromAdapter.getBasePairs().stream()
                .filter(basePair -> !basePair.isCanonical())
                .map(pair -> BasePairDTO.ofBasePairDTOWithNameFromMap(pair, pairIdentifiersWithTheirShortNames))
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair)
                        .withInteractionType(InteractionType.BASE_BASE)
                        .withSaenger(SaengerType.mapToBioCommonsForm(basePair.getSaengerType()))
                        .withLeontisWesthof(LeontisWesthofType.mapToBioCommonsForm(basePair.getLeontisWesthofType()))
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)
                        .withStackingTopology(StackingTopology.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> stackings = responseFromAdapter.getStackings().stream()
                .map(pair -> BasePairDTO.ofBasePairDTOWithNameFromMap(pair, pairIdentifiersWithTheirShortNames))
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair)
                        .withInteractionType(InteractionType.STACKING)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)
                        .withStackingTopology(mapToBioCommonsForm(basePair.getTopology())))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> basePhosphate = responseFromAdapter.getBasePhosphateInteractions().stream()
                .map(pair -> BasePairDTO.ofBasePairDTOWithNameFromMap(pair, pairIdentifiersWithTheirShortNames))
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair)
                        .withInteractionType(InteractionType.BASE_PHOSPHATE)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(BasePhosphateType.mapToBioCommonsForm(basePair.getBph()))
                        .withBr(BR.UNKNOWN)
                        .withStackingTopology(StackingTopology.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> baseRibose = responseFromAdapter.getBaseRiboseInteractions().stream()
                .map(pair -> BasePairDTO.ofBasePairDTOWithNameFromMap(pair, pairIdentifiersWithTheirShortNames))
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair)
                        .withInteractionType(InteractionType.BASE_RIBOSE)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(BPh.UNKNOWN)
                        .withBr(BaseRiboseType.mapToBioCommonsForm(basePair.getBr()))
                        .withStackingTopology(StackingTopology.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> otherInteractions = responseFromAdapter.getOther().stream()
                .map(pair -> BasePairDTO.ofBasePairDTOWithNameFromMap(pair, pairIdentifiersWithTheirShortNames))
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair)
                        .withInteractionType(InteractionType.OTHER)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)
                        .withStackingTopology(StackingTopology.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> interStrand = responseFromAdapter.getBasePairs().stream()
                .map(pair -> BasePairDTO.ofBasePairDTOWithNameFromMap(pair, pairIdentifiersWithTheirShortNames))
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair)
                        .withInteractionType(InteractionType.BASE_BASE)
                        .withSaenger(SaengerType.mapToBioCommonsForm(basePair.getSaengerType()))
                        .withLeontisWesthof(LeontisWesthofType.mapToBioCommonsForm(basePair.getLeontisWesthofType()))
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)
                        .withStackingTopology(StackingTopology.UNKNOWN))
                .filter(basePair -> !basePair.basePair()
                        .left()
                        .chainIdentifier()
                        .equals(basePair.basePair().right().chainIdentifier()))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> represented = determineRepresentedPairs(canonical, nonCanonical, includeNonCanonical);

        MultipletSet multipletSet = new MultipletSet(
                Stream.of(canonical, nonCanonical).flatMap(Collection::stream).collect(Collectors.toList()));
        List<String> messages = multipletSet.generateMessages();

        return new BasePairAnalysis.BasePairAnalysisBuilder()
                .withRepresented(represented)
                .withCanonical(canonical)
                .withNonCanonical(nonCanonical)
                .withStacking(stackings)
                .withBasePhosphate(basePhosphate)
                .withBaseRibose(baseRibose)
                .withOther(otherInteractions)
                .withInterStrand(interStrand)
                .withMessages(messages)
                .build();
    }

    private List<AnalyzedBasePair> determineRepresentedPairs(
            List<AnalyzedBasePair> canonicalPairs,
            List<AnalyzedBasePair> nonCanonicalPairs,
            boolean includeNonCanonical) {
        List<AnalyzedBasePair> pairsClassifiedAsRepresented = new ArrayList<>();
        final HashSet<PdbNamedResidueIdentifier> classifiedNucleotides = new HashSet<>();

        Consumer<AnalyzedBasePair> classifyBasePair = pair -> {
            if (!classifiedNucleotides.contains(pair.basePair().left())
                    && !classifiedNucleotides.contains(pair.basePair().right())) {
                pairsClassifiedAsRepresented.add(pair);
                /* TODO: ask if this should stay (2.0 version has inverted pairs, however it probably does not matter)
                    pairsClassifiedAsRepresented.add((AnalyzedBasePair) pair.invert());
                */
                classifiedNucleotides.add(pair.basePair().left());
                classifiedNucleotides.add(pair.basePair().right());
            }
        };

        canonicalPairs.forEach(classifyBasePair);
        if (includeNonCanonical) {
            nonCanonicalPairs.forEach(classifyBasePair);
        }
        return pairsClassifiedAsRepresented;
    }

    protected BasePairAnalyzer(RnaPDBeeAdaptersCaller rnapdbeeAdaptersCaller) {
        this.rnapdbeeAdaptersCaller = rnapdbeeAdaptersCaller;
    }
}
