package pl.poznan.put.rnapdbee.engine.basepair.boundary;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.put.rnapdbee.analysis.AnalysisResult;
import edu.put.rnapdbee.analysis.BasePairAnalyzer;
import edu.put.rnapdbee.analysis.TertiaryAnalysisResult;
import edu.put.rnapdbee.enums.InputType;
import edu.put.rnapdbee.enums.NonCanonicalHandling;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.basepair.model.AnalysisOutput;
import pl.poznan.put.structure.AnalyzedBasePair;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


// TODO tidy up the abstraction hierarchy when the rnapdbee-common code is incorporated into engine's
//  also WebFlux would be really efficient with the 3D->multi 2D analysis as we there perform multiple calls to the
//  adapters, it could be done in parallel and then joined up after each call is performed. We would save a tone of
//  time with this approach if the adapters were scalable horizontally
public class MCAnnotateBasePairAnalyzer implements BasePairAnalyzer {


    @Override
    public AnalysisResult analyze(InputType inputType,
                                  PdbModel pdbModel,
                                  String fileContent,
                                  NonCanonicalHandling nonCanonicalHandling) throws PdbParsingException, IOException {
        // TODO put somewhere this MapperFeature not to have it at every place.
        final ObjectMapper mapper = new ObjectMapper()
                .findAndRegisterModules().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .jackson2JsonDecoder(new Jackson2JsonDecoder(mapper)))
                .build();

        WebClient webClient = WebClient
                .builder().exchangeStrategies(exchangeStrategies)
                // TODO take from configuration
                .baseUrl("http://localhost:8000")
                .build();


        AnalysisOutput responseFromAdapter = webClient
                .post()
                // TODO take from configuration
                .uri("analyze/mc-annotate")
                .contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromValue(fileContent))
                .retrieve()
                .bodyToMono(AnalysisOutput.class)
                .block();

        List<AnalyzedBasePair> represented = Collections.emptyList();
        List<AnalyzedBasePair> canonical = Collections.emptyList();
        List<AnalyzedBasePair> nonCanonical = Collections.emptyList();
        List<AnalyzedBasePair> stackings = Collections.emptyList();
        List<AnalyzedBasePair> basePhosphate = Collections.emptyList();
        List<AnalyzedBasePair> baseRibose = Collections.emptyList();
        List<AnalyzedBasePair> otherInteractions = Collections.emptyList();
        List<AnalyzedBasePair> interStrand = Collections.emptyList();
        List<String> messages = Collections.emptyList();

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
}
