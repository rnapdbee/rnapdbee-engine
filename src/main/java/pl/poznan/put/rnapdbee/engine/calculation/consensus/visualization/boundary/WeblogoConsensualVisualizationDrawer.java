package pl.poznan.put.rnapdbee.engine.calculation.consensus.visualization.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.visualization.domain.AdaptersConsensualVisualizationPayload;
import pl.poznan.put.rnapdbee.engine.infrastructure.configuration.RnapdbeeAdaptersProperties;

import java.time.Duration;
import java.util.List;

@Service
public class WeblogoConsensualVisualizationDrawer implements ConsensualVisualizationDrawer {

    private final RnapdbeeAdaptersProperties properties;
    private final WebClient webClient;

    @Override
    public byte[] performVisualization(List<OutputMultiEntry> outputMultiEntries) {

        AdaptersConsensualVisualizationPayload payload = AdaptersConsensualVisualizationPayload.of(outputMultiEntries);

        return webClient
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
    public WeblogoConsensualVisualizationDrawer(
            RnapdbeeAdaptersProperties properties,
            @Autowired @Qualifier("adaptersWebClient") WebClient adaptersWebClient) {
        this.webClient = adaptersWebClient;
        this.properties = properties;
    }
}
