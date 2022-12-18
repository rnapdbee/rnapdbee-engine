package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.notation.StackingTopology;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.rna.InteractionType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.AdaptersAnalysisDTO;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairDTO;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RnaPDBeeAdaptersCaller;
import pl.poznan.put.rnapdbee.engine.shared.multiplet.MultipletSet;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.ImmutableAnalyzedBasePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.poznan.put.rnapdbee.engine.shared.basepair.domain.StackingTopology.convertToBioCommonsEntity;

// TODO: WebFlux would be really efficient with the 3D->multi 2D analysis as we there perform multiple calls to the
//  adapters, it could be done in parallel and then joined up after each call is performed. We would save a tone of
//  time with this approach if the adapters were scalable horizontally in the future.
public abstract class BasePairAnalyzer {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final RnaPDBeeAdaptersCaller rnapdbeeAdaptersCaller;

    public abstract AnalysisTool analysisTool();

    // TODO: think about using WebFlux advancements when refactoring
    public abstract BasePairAnalysis analyze(String fileContent,
                                             boolean includeNonCanonical,
                                             int modelNumber);

    protected BasePairAnalysis performAnalysis(String fileContent,
                                               boolean includeNonCanonical,
                                               int modelNumber) {
        logger.info(String.format("base pair analysis started for model number %s", modelNumber));
        AdaptersAnalysisDTO adaptersAnalysis = rnapdbeeAdaptersCaller
                .performBasePairAnalysis(fileContent, analysisTool(), modelNumber);
        return performPostAnalysisOnResponseFromAdapter(adaptersAnalysis, includeNonCanonical);
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
    protected BasePairAnalysis performPostAnalysisOnResponseFromAdapter(AdaptersAnalysisDTO responseFromAdapter,
                                                                        boolean includeNonCanonical) {
        List<AnalyzedBasePair> canonical = responseFromAdapter.getBasePairs().stream()
                .filter(BasePairDTO::isCanonical)
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair)
                        .withInteractionType(InteractionType.BASE_BASE)
                        .withSaenger(basePair.getSaenger())
                        .withLeontisWesthof(basePair.getLeontisWesthof())
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)
                        .withStackingTopology(StackingTopology.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> nonCanonical = responseFromAdapter.getBasePairs().stream()
                .filter(basePair -> !basePair.isCanonical())
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair)
                        .withInteractionType(InteractionType.BASE_BASE)
                        .withSaenger(basePair.getSaenger())
                        .withLeontisWesthof(basePair.getLeontisWesthof())
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)
                        .withStackingTopology(StackingTopology.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> stackings = responseFromAdapter.getStackings().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.STACKING)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)
                        .withStackingTopology(convertToBioCommonsEntity(basePair.getTopology())))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> basePhosphate = responseFromAdapter.getBasePhosphateInteractions().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.BASE_PHOSPHATE)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BPh.mapToBioCommonsBph(basePair.getBph()))
                        .withBr(BR.UNKNOWN)
                        .withStackingTopology(StackingTopology.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> baseRibose = responseFromAdapter.getBaseRiboseInteractions().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.BASE_RIBOSE)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(BPh.UNKNOWN)
                        .withBr(pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BR.mapToBioCommonsBr(basePair.getBr()))
                        .withStackingTopology(StackingTopology.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> otherInteractions = responseFromAdapter.getOther().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.OTHER)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)
                        .withStackingTopology(StackingTopology.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> interStrand = responseFromAdapter.getBasePairs().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.BASE_BASE)
                        .withSaenger(basePair.getSaenger())
                        .withLeontisWesthof(basePair.getLeontisWesthof())
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)
                        .withStackingTopology(StackingTopology.UNKNOWN))
                .filter(basePair -> !basePair.basePair().left().chainIdentifier().equals(basePair.basePair().right().chainIdentifier()))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> represented = determineRepresentedPairs(canonical, nonCanonical, includeNonCanonical);

        final Iterable<AnalyzedBasePair> allBasePairs =
                (includeNonCanonical
                        ? Stream.of(canonical, nonCanonical, stackings, basePhosphate, baseRibose, otherInteractions)
                        : Stream.of(canonical, stackings, basePhosphate, baseRibose, otherInteractions)
                )
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
        List<String> messages = (new MultipletSet(allBasePairs)).generateMessages();

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

    private List<AnalyzedBasePair> determineRepresentedPairs(List<AnalyzedBasePair> canonicalPairs,
                                                             List<AnalyzedBasePair> nonCanonicalPairs,
                                                             boolean includeNonCanonical) {
        List<AnalyzedBasePair> pairsClassifiedAsRepresented = new ArrayList<>();
        final HashSet<PdbNamedResidueIdentifier> classifiedNucleotides = new HashSet<>();

        Consumer<AnalyzedBasePair> classifyBasePair = pair -> {
            if (!classifiedNucleotides.contains(pair.basePair().left()) &&
                    !classifiedNucleotides.contains(pair.basePair().right())) {
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
