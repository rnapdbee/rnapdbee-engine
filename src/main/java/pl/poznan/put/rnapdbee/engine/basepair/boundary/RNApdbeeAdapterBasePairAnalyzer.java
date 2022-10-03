package pl.poznan.put.rnapdbee.engine.basepair.boundary;

import edu.put.rnapdbee.analysis.AnalysisResult;
import edu.put.rnapdbee.analysis.BasePairAnalyzer;
import edu.put.rnapdbee.analysis.TertiaryAnalysisResult;
import edu.put.rnapdbee.analysis.multiplet.MultipletSet;
import edu.put.rnapdbee.enums.InputType;
import edu.put.rnapdbee.enums.NonCanonicalHandling;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rna.InteractionType;
import pl.poznan.put.rnapdbee.engine.basepair.model.AdaptersAnalysisDTO;
import pl.poznan.put.rnapdbee.engine.basepair.model.BasePairDTO;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.ImmutableAnalyzedBasePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO tidy up the abstraction hierarchy when the rnapdbee-common code is incorporated into engine's.
//  also WebFlux would be really efficient with the 3D->multi 2D analysis as we there perform multiple calls to the
//  adapters, it could be done in parallel and then joined up after each call is performed. We would save a tone of
//  time with this approach if the adapters were scalable horizontally in the future.
public abstract class RNApdbeeAdapterBasePairAnalyzer implements BasePairAnalyzer {

    WebClient adaptersWebClient;

    /**
     * URI/path of the specific adapter - e.g. analyze/mc-annotate. Set in this class' implementation.
     */
    protected String adapterURI;

    // TODO: think about using WebFlux advancements when refactoring
    @Override
    public AnalysisResult analyze(InputType inputType,
                                  PdbModel pdbModel,
                                  String fileContent,
                                  NonCanonicalHandling nonCanonicalHandling) throws PdbParsingException {
        AdaptersAnalysisDTO adaptersAnalysis = performAnalysisOnAdapter(fileContent);
        assert adaptersAnalysis != null;

        return performPostAnalysisOnResponseFromAdapter(adaptersAnalysis, nonCanonicalHandling);
    }

    /**
     * performs post analysis on response from adapter.
     * Separates response into canonical, nonCanonical, stackings, basePhosphate, baseRibose, otherInteractions,
     * and interStrand lists of base pairs.
     * <p>
     * Calculates represented list of base pairs.
     * Calculates messages list.
     *
     * @param responseFromAdapter  response from the adapter
     * @param nonCanonicalHandling enum specifying how should nonCanonical pairs be handled
     * @return {@link AnalysisResult} TertiaryAnalysisResult object with populated pairs lists.
     */
    private AnalysisResult performPostAnalysisOnResponseFromAdapter(AdaptersAnalysisDTO responseFromAdapter,
                                                                    NonCanonicalHandling nonCanonicalHandling) {
        List<AnalyzedBasePair> canonical = responseFromAdapter.getBasePairs().stream()
                .filter(BasePairDTO::isCanonical)
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair)
                        .withInteractionType(InteractionType.BASE_BASE)
                        .withSaenger(basePair.getSaenger())
                        .withLeontisWesthof(basePair.getLeontisWesthof())
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> nonCanonical = responseFromAdapter.getBasePairs().stream()
                .filter(basePair -> !basePair.isCanonical())
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair)
                        .withInteractionType(InteractionType.BASE_BASE)
                        .withSaenger(basePair.getSaenger())
                        .withLeontisWesthof(basePair.getLeontisWesthof())
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> stackings = responseFromAdapter.getStackings().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.STACKING)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> basePhosphate = responseFromAdapter.getBasePhosphateInteractions().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.BASE_PHOSPHATE)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(pl.poznan.put.rnapdbee.engine.basepair.model.BPh.mapToBioCommonsBph(basePair.getBph()))
                        .withBr(BR.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> baseRibose = responseFromAdapter.getBaseRiboseInteractions().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.BASE_RIBOSE)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(BPh.UNKNOWN)
                        .withBr(pl.poznan.put.rnapdbee.engine.basepair.model.BR.mapToBioCommonsBr(basePair.getBr())))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> otherInteractions = responseFromAdapter.getOther().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.OTHER)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> interStrand = responseFromAdapter.getBasePairs().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.BASE_BASE)
                        .withSaenger(basePair.getSaenger())
                        .withLeontisWesthof(basePair.getLeontisWesthof())
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN))
                .filter(basePair -> !basePair.basePair().left().chainIdentifier().equals(basePair.basePair().right().chainIdentifier()))
                .collect(Collectors.toList());
        List<AnalyzedBasePair> represented = determineRepresentedPairs(canonical, nonCanonical, nonCanonicalHandling);

        final Iterable<AnalyzedBasePair> allBasePairs =
                (nonCanonicalHandling == NonCanonicalHandling.ANALYZE_VISUALIZE
                        ? Stream.of(canonical, nonCanonical, stackings, basePhosphate, baseRibose, otherInteractions)
                        : Stream.of(canonical, stackings, basePhosphate, baseRibose, otherInteractions)
                )
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
        List<String> messages = (new MultipletSet(allBasePairs)).generateMessages();

        return new TertiaryAnalysisResult(
                nonCanonicalHandling,
                represented,
                canonical,
                nonCanonical,
                stackings,
                basePhosphate,
                baseRibose,
                otherInteractions,
                interStrand,
                messages
        );
    }

    /**
     * Calls rnapdbee-adapters in order to analyse the fileContent.
     *
     * @param fileContent content of file
     * @return {@link AdaptersAnalysisDTO} - performed analysis as Java object
     */
    private AdaptersAnalysisDTO performAnalysisOnAdapter(String fileContent) {
        return adaptersWebClient
                .post()
                .uri(adapterURI)
                .contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromValue(fileContent))
                .retrieve()
                .bodyToMono(AdaptersAnalysisDTO.class)
                .block();
    }

    private List<AnalyzedBasePair> determineRepresentedPairs(List<AnalyzedBasePair> canonicalPairs,
                                                             List<AnalyzedBasePair> nonCanonicalPairs,
                                                             NonCanonicalHandling nonCanonicalHandling) {
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
        if (nonCanonicalHandling == NonCanonicalHandling.ANALYZE_VISUALIZE) {
            nonCanonicalPairs.forEach(classifyBasePair);
        }
        return pairsClassifiedAsRepresented;
    }

    RNApdbeeAdapterBasePairAnalyzer(WebClient adaptersWebClient) {
        this.adaptersWebClient = adaptersWebClient;
    }
}
