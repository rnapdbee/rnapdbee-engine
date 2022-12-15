package pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.domain.AdaptersConsensualVisualizationPayload;
import pl.poznan.put.rnapdbee.engine.infrastructure.configuration.RnapdbeeAdaptersProperties;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.AdaptersAnalysisDTO;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.component.PathDeterminer;

import java.time.Duration;
import java.util.List;

@Component
public class RNApdbeeAdaptersCaller {

    private final RnapdbeeAdaptersProperties properties;
    private final WebClient adaptersWebClient;
    private final PathDeterminer pathDeterminer;

    /**
     * Calls rnapdbee-adapters in order to receive base pair analysis of fileContent.
     *
     * @param fileContent content of file
     * @return {@link AdaptersAnalysisDTO} - performed analysis as Java object
     */
    public AdaptersAnalysisDTO performBasePairAnalysis(String fileContent,
                                                       AnalysisTool analysisTool,
                                                       int modelNumber) {

        String adapterUri = pathDeterminer.determinePath(analysisTool);

        return adaptersWebClient
                .post()
                .uri(adapterUri, modelNumber)
                .contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromValue(fileContent))
                .retrieve()
                .bodyToMono(AdaptersAnalysisDTO.class)
                .cache(Duration.ofSeconds(properties.getMonoCacheDurationInSeconds()))
                .block();
    }

    /**
     * calls rnapdbee-adapters in order to receive weblogo visualization of output multi entries.
     *
     * @param outputMultiEntries given input
     * @return binary representation of the file
     */
    public byte[] performWeblogoVisualization(List<OutputMultiEntry> outputMultiEntries) {

        AdaptersConsensualVisualizationPayload payload = AdaptersConsensualVisualizationPayload.of(outputMultiEntries);

        return adaptersWebClient
                .post()
                .uri(properties.getWeblogoPath())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payload))
                .retrieve()
                .bodyToMono(byte[].class)
                .cache(Duration.ofSeconds(properties.getMonoCacheDurationInSeconds()))
                .block();
    }

    @Autowired
    public RNApdbeeAdaptersCaller(RnapdbeeAdaptersProperties properties,
                                  @Autowired @Qualifier("adaptersWebClient") WebClient adaptersWebClient,
                                  PathDeterminer pathDeterminer) {
        this.properties = properties;
        this.adaptersWebClient = adaptersWebClient;
        this.pathDeterminer = pathDeterminer;
    }
}
