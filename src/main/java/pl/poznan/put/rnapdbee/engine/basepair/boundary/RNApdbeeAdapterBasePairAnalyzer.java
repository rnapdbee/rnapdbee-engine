package pl.poznan.put.rnapdbee.engine.basepair.boundary;

import edu.put.rnapdbee.analysis.AnalysisResult;
import edu.put.rnapdbee.analysis.BasePairAnalyzer;
import edu.put.rnapdbee.analysis.TertiaryAnalysisResult;
import edu.put.rnapdbee.enums.InputType;
import edu.put.rnapdbee.enums.NonCanonicalHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rna.InteractionType;
import pl.poznan.put.rnapdbee.engine.basepair.model.AdaptersAnalysisDTO;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.ImmutableAnalyzedBasePair;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO tidy up the abstraction hierarchy when the rnapdbee-common code is incorporated into engine's.
//  also WebFlux would be really efficient with the 3D->multi 2D analysis as we there perform multiple calls to the
//  adapters, it could be done in parallel and then joined up after each call is performed. We would save a tone of
//  time with this approach if the adapters were scalable horizontally in the future.
public abstract class RNApdbeeAdapterBasePairAnalyzer implements BasePairAnalyzer {

    @Autowired
    @Qualifier("adaptersWebClient")
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
     * interStrand and represented lists of base pairs.
     *
     * TODO: clarify how to retrieve represented and messages.
     * @param responseFromAdapter   response from the adapter
     * @param nonCanonicalHandling  enum specifying how should nonCanonical pairs be handled
     * @return  {@link AnalysisResult} TertiaryAnalysisResult object with populated pairs lists.
     */
    private AnalysisResult performPostAnalysisOnResponseFromAdapter(AdaptersAnalysisDTO responseFromAdapter,
                                                                    NonCanonicalHandling nonCanonicalHandling) {
        List<AnalyzedBasePair> canonical = responseFromAdapter.getBasePairs().stream().filter(basePair -> basePair.getSaenger().isCanonical())
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.BASE_BASE)
                        .withSaenger(basePair.getSaenger())
                        .withLeontisWesthof(basePair.getLeontisWesthof())
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)).collect(Collectors.toList());
        List<AnalyzedBasePair> nonCanonical = responseFromAdapter.getBasePairs().stream().filter(basePair -> !basePair.getSaenger().isCanonical())
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.BASE_BASE)
                        .withSaenger(basePair.getSaenger())
                        .withLeontisWesthof(basePair.getLeontisWesthof())
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)).collect(Collectors.toList());
        List<AnalyzedBasePair> stackings = responseFromAdapter.getStackings().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.STACKING)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)).collect(Collectors.toList());
        List<AnalyzedBasePair> basePhosphate = responseFromAdapter.getBasePhosphateInteractions().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.BASE_PHOSPHATE)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)).collect(Collectors.toList());
        List<AnalyzedBasePair> baseRibose = responseFromAdapter.getBaseRiboseInteractions().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.BASE_RIBOSE)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)).collect(Collectors.toList());
        List<AnalyzedBasePair> otherInteractions = responseFromAdapter.getOther().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.OTHER)
                        .withSaenger(Saenger.UNKNOWN)
                        .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN)).collect(Collectors.toList());
        List<AnalyzedBasePair> interStrand = responseFromAdapter.getBasePairs().stream()
                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair).withInteractionType(InteractionType.BASE_BASE)
                        .withSaenger(basePair.getSaenger())
                        .withLeontisWesthof(basePair.getLeontisWesthof())
                        .withBph(BPh.UNKNOWN)
                        .withBr(BR.UNKNOWN))
                .filter(basePair -> !basePair.basePair().left().chainIdentifier().equals(basePair.basePair().right().chainIdentifier()))
                .collect(Collectors.toList());
        List<String> messages = Collections.emptyList();

        //List<AnalyzedBasePair> represented =  Collections.emptyList();
        /*Map<PdbNamedResidueIdentifier, PdbNamedResidueIdentifier> uniqueBasePairs = new HashMap<>();
        Stream.of(canonical, nonCanonical, stackings, basePhosphate, baseRibose, otherInteractions)
                .flatMap(Collection::stream)
                .flatMap(analyzedBasePair -> Stream.of(analyzedBasePair.basePair(), analyzedBasePair.basePair().invert()))
                .forEach(analyzedBasePair -> uniqueBasePairs.put(analyzedBasePair.left(), analyzedBasePair.right()));*/
        // TODO: this is wrong. With this approach, in further analysis there is pair 0 - 0, which throws an error
        //  inside bioCommons method. Change approach when calculation of represented list is clarified.
        List<AnalyzedBasePair> represented =
                nonCanonicalHandling == NonCanonicalHandling.IGNORE
                        ?
                        Stream.of(canonical, stackings, basePhosphate, baseRibose, otherInteractions)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList())
                        :
                        Stream.of(canonical, nonCanonical, stackings, basePhosphate, baseRibose, otherInteractions)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList());

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


    /*
    TODO: For represented, we need to have this behaviour: (copied from common)
    private List<AnalyzedBasePair> remapBasePairsToClassified() {
        final Map<BasePair, AnalyzedBasePair> pairClassification = new HashMap<>();
        for (final AnalyzedBasePair classifiedBasePair : anyBasePair) {
            final BasePair basePair = classifiedBasePair.basePair();
            pairClassification.put(basePair, classifiedBasePair);
            pairClassification.put(basePair.invert(), (AnalyzedBasePair) classifiedBasePair.invert());
        }

        return mapBasePairs.entrySet().stream()
                .map(entry -> ImmutableBasePair.of(entry.getKey(), entry.getValue()))
                .map(pairClassification::get)
                .collect(Collectors.toList());
    }
    */
}
