package pl.poznan.put.rnapdbee.engine.basepair.boundary;

import edu.put.rnapdbee.analysis.AnalysisResult;
import edu.put.rnapdbee.analysis.BasePairAnalyzer;
import edu.put.rnapdbee.analysis.TertiaryAnalysisResult;
import edu.put.rnapdbee.enums.InputType;
import edu.put.rnapdbee.enums.NonCanonicalHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rna.InteractionType;
import pl.poznan.put.rnapdbee.engine.basepair.model.AnalysisOutput;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.ImmutableAnalyzedBasePair;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


// TODO tidy up the abstraction hierarchy when the rnapdbee-common code is incorporated into engine's
//  also WebFlux would be really efficient with the 3D->multi 2D analysis as we there perform multiple calls to the
//  adapters, it could be done in parallel and then joined up after each call is performed. We would save a tone of
//  time with this approach if the adapters were scalable horizontally
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MCAnnotateBasePairAnalyzer implements BasePairAnalyzer {

    @Autowired
    @Qualifier("adaptersWebClient")
    WebClient adaptersWebClient;


    @Override
    public AnalysisResult analyze(InputType inputType,
                                  PdbModel pdbModel,
                                  String fileContent,
                                  NonCanonicalHandling nonCanonicalHandling) throws PdbParsingException, IOException {
        // TODO put somewhere this MapperFeature not to have it at every place.


        AnalysisOutput responseFromAdapter = adaptersWebClient
                .post()
                // TODO take from configuration
                .uri("analyze/mc-annotate")
                .contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromValue(fileContent))
                .retrieve()
                .bodyToMono(AnalysisOutput.class)
                .block();
        assert responseFromAdapter != null;

        List<AnalyzedBasePair> represented =  Collections.emptyList();
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
        List<AnalyzedBasePair> interStrand = Collections.emptyList();
        List<String> messages = Collections.emptyList();

        /*
        List<AnalyzedBasePair> represented = Stream.of(canonical, nonCanonical, stackings, basePhosphate, baseRibose, otherInteractions)
                .flatMap(Collection::stream)
                .map(basePair -> Pair.of(basePair.basePair()));
                */

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

    /*
    TODO For represented, we need to have this behaviour: (copied from common)
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
