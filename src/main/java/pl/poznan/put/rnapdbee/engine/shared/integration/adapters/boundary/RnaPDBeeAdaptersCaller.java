package pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.engine.shared.exception.AdaptersErrorException;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.domain.AdaptersConsensualVisualizationPayload;
import pl.poznan.put.rnapdbee.engine.infrastructure.configuration.RnaPDBeeAdaptersProperties;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.AdaptersAnalysisDTO;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.component.PathDeterminer;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.domain.AdaptersVisualizationPayload;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.utility.svg.SVGHelper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Component
public class RnaPDBeeAdaptersCaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(RnaPDBeeAdaptersCaller.class);

    private static final String ERROR_4XX_GOTTEN_FROM_ADAPTERS_FORMAT =
            "Error 4XX: %s received from rnapdbee-adapters. Full response: %s";
    private static final String ERROR_5XX_GOTTEN_FROM_ADAPTERS_FORMAT =
            "Error 5XX: %s received from rnapdbee-adapters. Full response: %s";

    private final RnaPDBeeAdaptersProperties properties;
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
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> clientResponse
                        .bodyToMono(String.class)
                        .map(resp -> {
                            LOGGER.warn(String.format(ERROR_4XX_GOTTEN_FROM_ADAPTERS_FORMAT,
                                    clientResponse.rawStatusCode(), resp));
                            return new AdaptersErrorException("Internal error has occurred");
                        }))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> clientResponse
                        .bodyToMono(String.class)
                        .map(resp -> {
                            LOGGER.warn(String.format(ERROR_5XX_GOTTEN_FROM_ADAPTERS_FORMAT,
                                    clientResponse.rawStatusCode(), resp));
                            return new AdaptersErrorException("Internal error has occurred");
                        }))
                .bodyToMono(AdaptersAnalysisDTO.class)
                .cache(Duration.ofSeconds(properties.getMonoCacheDurationInSeconds()))
                .onErrorMap(WebClientRequestException.class, (exception) -> {
                    LOGGER.warn("WebClient exception occurred");
                    return new AdaptersErrorException("Internal error has occurred", exception);
                })
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

    public SVGDocument performVisualization(DotBracket dotBracket,
                                            PdbModel pdbModel,
                                            VisualizationTool visualizationTool,
                                            List<? extends ClassifiedBasePair> nonCanonicalPairs) throws IOException {

        String adapterUri = pathDeterminer.determinePath(visualizationTool);

        AdaptersVisualizationPayload adaptersVisualizationPayload = AdaptersVisualizationPayload.of(
                dotBracket,
                pdbModel,
                nonCanonicalPairs);

        return performVisualizationCall(adapterUri, adaptersVisualizationPayload);
    }

    public SVGDocument performVisualization(DotBracket dotBracket, VisualizationTool visualizationTool) throws IOException {

        String adapterUri = pathDeterminer.determinePath(visualizationTool);

        AdaptersVisualizationPayload adaptersVisualizationPayload = AdaptersVisualizationPayload.of(dotBracket);

        return performVisualizationCall(adapterUri, adaptersVisualizationPayload);
    }

    private SVGDocument performVisualizationCall(String adapterUri,
                                                 AdaptersVisualizationPayload adaptersVisualizationPayload) throws IOException {
        byte[] adaptersResponse = adaptersWebClient
                .post()
                .uri(adapterUri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(adaptersVisualizationPayload))
                .retrieve()
                .bodyToMono(byte[].class)
                .cache(Duration.ofSeconds(properties.getMonoCacheDurationInSeconds()))
                .block();

        if (adaptersResponse == null) {
            throw new RuntimeException("Response from rnapdbee-adapters is null");
        }

        File tempFile = null;
        try {
            tempFile = File.createTempFile("adapters-response", ".svg");
            FileUtils.writeByteArrayToFile(tempFile, adaptersResponse);
            return SVGHelper.fromFile(tempFile);
        } catch (IOException e) {
            LOGGER.error("Exception thrown when saving rnapdbee-adapters result", e);
            throw e;
        } finally {
            if (tempFile != null) {
                FileUtils.forceDelete(tempFile);
            }
        }
    }

    @Autowired
    public RnaPDBeeAdaptersCaller(RnaPDBeeAdaptersProperties properties,
                                  @Autowired @Qualifier("adaptersWebClient") WebClient adaptersWebClient,
                                  PathDeterminer pathDeterminer) {
        this.properties = properties;
        this.adaptersWebClient = adaptersWebClient;
        this.pathDeterminer = pathDeterminer;
    }
}
