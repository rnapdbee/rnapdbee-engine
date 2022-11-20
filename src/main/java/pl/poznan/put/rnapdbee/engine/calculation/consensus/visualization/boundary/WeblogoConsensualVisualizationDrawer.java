package pl.poznan.put.rnapdbee.engine.calculation.consensus.visualization.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.visualization.domain.AdaptersConsensualVisualizationPayload;

import java.time.Duration;
import java.util.List;

@Service
public class WeblogoConsensualVisualizationDrawer implements ConsensualVisualizationDrawer {

    private final String pathToWeblogoEndpoint;
    private final WebClient webClient;

    @Override
    public byte[] performVisualization(List<OutputMultiEntry> outputMultiEntries) {

        AdaptersConsensualVisualizationPayload payload = AdaptersConsensualVisualizationPayload.of(outputMultiEntries);

        return webClient
                .post()
                .uri(pathToWeblogoEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payload))
                .retrieve()
                .bodyToMono(byte[].class)
                .cache(Duration.ofSeconds(240))
                .block();
    }

    @Autowired
    public WeblogoConsensualVisualizationDrawer(
            @Autowired @Qualifier("adaptersWebClient") WebClient adaptersWebClient,
            @Value("${rnapdbee.adapters.global.weblogo.path}") String pathToWeblogoEndpoint) {
        this.webClient = adaptersWebClient;
        this.pathToWeblogoEndpoint = pathToWeblogoEndpoint;
    }
}
