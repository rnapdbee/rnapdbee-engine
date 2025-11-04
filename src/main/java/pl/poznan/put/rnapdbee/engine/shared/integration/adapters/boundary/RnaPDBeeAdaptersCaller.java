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
import org.springframework.web.reactive.function.client.WebClientException;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.engine.shared.basepair.exception.AdaptersErrorException;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.image.exception.VisualizationException;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.domain.AdaptersConsensualVisualizationPayload;
import pl.poznan.put.rnapdbee.engine.infrastructure.configuration.RnaPDBeeAdaptersProperties;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.AdaptersAnalysisDTO;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.component.PathDeterminer;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.domain.AdaptersVisualizationPayload;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.ImmutableDefaultDotBracket;
import pl.poznan.put.utility.svg.SVGHelper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Component
public class RnaPDBeeAdaptersCaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(RnaPDBeeAdaptersCaller.class);

    private static final String ERROR_4XX_GOTTEN_FROM_ADAPTERS_FORMAT =
            "Error 4XX: %s received from rnapdbee-adapters. Full response: %s";
    private static final String ERROR_5XX_GOTTEN_FROM_ADAPTERS_FORMAT =
            "Error 5XX: %s received from rnapdbee-adapters. Full response: %s";
    private static final String ERROR_STATUS_GOTTEN_FROM_ADAPTERS_FORMAT = "%s response gotten from rnapdbee-adapters";
    private static final String ERROR_MET_DURING_CALL_TO_ADAPTERS_LOG = "Error met when calling rnapdbee-adapters:";
    private static final String ERROR_MET_DURING_CALL_TO_ADAPTERS = "Error met when calling rnapdbee-adapters";
    private static final String RESPONSE_IS_NULL = "Response from rnapdbee-adapters is null";
    private static final String IOEXCEPTION_WHEN_SAVING_SVG = "IOException thrown when creating SVG document";

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
                                                       int modelNumber) throws AdaptersErrorException {

        String adapterUri = pathDeterminer.determinePath(analysisTool);

        try {
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
                                return new IllegalStateException(String.format(
                                        ERROR_STATUS_GOTTEN_FROM_ADAPTERS_FORMAT, clientResponse.rawStatusCode()));
                            }))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> clientResponse
                            .bodyToMono(String.class)
                            .map(resp -> {
                                LOGGER.warn(String.format(ERROR_5XX_GOTTEN_FROM_ADAPTERS_FORMAT,
                                        clientResponse.rawStatusCode(), resp));
                                return new IllegalStateException(String.format(
                                        ERROR_STATUS_GOTTEN_FROM_ADAPTERS_FORMAT, clientResponse.rawStatusCode()));
                            }))
                    .bodyToMono(AdaptersAnalysisDTO.class)
                    .cache(Duration.ofSeconds(properties.getMonoCacheDurationInSeconds()))
                    .block();
        } catch (WebClientException | IllegalStateException exception) {
            LOGGER.error(ERROR_MET_DURING_CALL_TO_ADAPTERS_LOG, exception);
            throw new AdaptersErrorException(ERROR_MET_DURING_CALL_TO_ADAPTERS, exception);
        }
    }

    /**
     * calls rnapdbee-adapters in order to receive weblogo visualization of output multi entries.
     *
     * @param outputMultiEntries given input
     * @return binary representation of the file
     */
    public byte[] performWeblogoVisualization(List<OutputMultiEntry> outputMultiEntries) throws VisualizationException {

        AdaptersConsensualVisualizationPayload payload = AdaptersConsensualVisualizationPayload.of(outputMultiEntries);

        try {
            return adaptersWebClient
                    .post()
                    .uri(properties.getWeblogoPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(payload))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> clientResponse
                            .bodyToMono(String.class)
                            .map(resp -> {
                                LOGGER.warn(String.format(ERROR_4XX_GOTTEN_FROM_ADAPTERS_FORMAT,
                                        clientResponse.rawStatusCode(), resp));
                                return new IllegalStateException(String.format(
                                        ERROR_STATUS_GOTTEN_FROM_ADAPTERS_FORMAT, clientResponse.rawStatusCode()));
                            }))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> clientResponse
                            .bodyToMono(String.class)
                            .map(resp -> {
                                LOGGER.warn(String.format(ERROR_5XX_GOTTEN_FROM_ADAPTERS_FORMAT,
                                        clientResponse.rawStatusCode(), resp));
                                return new IllegalStateException(String.format(
                                        ERROR_STATUS_GOTTEN_FROM_ADAPTERS_FORMAT, clientResponse.rawStatusCode()));
                            }))
                    .bodyToMono(byte[].class)
                    .cache(Duration.ofSeconds(properties.getMonoCacheDurationInSeconds()))
                    .block();
        } catch (WebClientException | IllegalStateException exception) {
            LOGGER.error(ERROR_MET_DURING_CALL_TO_ADAPTERS_LOG, exception);
            throw new VisualizationException(ERROR_MET_DURING_CALL_TO_ADAPTERS, exception);
        }
    }

    /**
     * Calls rnapdbee-adapters to perform non-canonical visualization
     *
     * @param dotBracket        structure to be drawn
     * @param pdbModel          pdb model of the structure
     * @param visualizationTool visualizationTool to be used
     * @param nonCanonicalPairs list of non-canonical pairs that are sent to the adapters
     * @return resulting SVG document
     * @throws VisualizationException if there is any error with the visualization
     */
    public SVGDocument performVisualization(DotBracket dotBracket,
                                            PdbModel pdbModel,
                                            VisualizationTool visualizationTool,
                                            List<? extends ClassifiedBasePair> nonCanonicalPairs)
            throws VisualizationException {
        String adapterUri = pathDeterminer.determinePath(visualizationTool);

        AdaptersVisualizationPayload adaptersVisualizationPayload = AdaptersVisualizationPayload.of(
                dotBracket,
                pdbModel,
                nonCanonicalPairs);

        return performVisualizationCall(adapterUri, adaptersVisualizationPayload);
    }

    /**
     * Calls rnapdbee-adapters to perform canonical visualization
     *
     * @param dotBracket        structure to be drawn
     * @param visualizationTool visualizationTool to be used
     * @return {@link SVGDocument} resulting SVG document
     * @throws VisualizationException if there is any error with the visualization
     */
    public SVGDocument performVisualization(DotBracket dotBracket, VisualizationTool visualizationTool)
            throws VisualizationException {
        String adapterUri = pathDeterminer.determinePath(visualizationTool);

        AdaptersVisualizationPayload adaptersVisualizationPayload = AdaptersVisualizationPayload.of(dotBracket);

        return performVisualizationCall(adapterUri, adaptersVisualizationPayload);
    }

    private SVGDocument performVisualizationCall(String adapterUri,
                                                 AdaptersVisualizationPayload adaptersVisualizationPayload)
            throws VisualizationException {

        byte[] adaptersResponse;
        try {
            adaptersResponse = adaptersWebClient
                    .post()
                    .uri(adapterUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(adaptersVisualizationPayload))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> clientResponse
                            .bodyToMono(String.class)
                            .map(resp -> {
                                LOGGER.warn(String.format(ERROR_4XX_GOTTEN_FROM_ADAPTERS_FORMAT,
                                        clientResponse.rawStatusCode(), resp));
                                return new IllegalStateException(String.format(
                                        ERROR_STATUS_GOTTEN_FROM_ADAPTERS_FORMAT, clientResponse.rawStatusCode()));
                            }))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> clientResponse
                            .bodyToMono(String.class)
                            .map(resp -> {
                                LOGGER.warn(String.format(ERROR_5XX_GOTTEN_FROM_ADAPTERS_FORMAT,
                                        clientResponse.rawStatusCode(), resp));
                                return new IllegalStateException(String.format(
                                        ERROR_STATUS_GOTTEN_FROM_ADAPTERS_FORMAT, clientResponse.rawStatusCode()));
                            }))
                    .bodyToMono(byte[].class)
                    .cache(Duration.ofSeconds(properties.getMonoCacheDurationInSeconds()))
                    .block();
        } catch (WebClientException | IllegalStateException exception) {
            LOGGER.error(ERROR_MET_DURING_CALL_TO_ADAPTERS_LOG, exception);
            throw new VisualizationException(ERROR_MET_DURING_CALL_TO_ADAPTERS, exception);
        }

        if (adaptersResponse == null) {
            LOGGER.error(RESPONSE_IS_NULL);
            throw new VisualizationException(RESPONSE_IS_NULL);
        }

        try {
            return createSvgDocument(adaptersResponse);
        } catch (IOException e) {
            throw new VisualizationException(IOEXCEPTION_WHEN_SAVING_SVG, e);
        }
    }

    private SVGDocument createSvgDocument(byte[] svgFileContent) throws IOException {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("adapters-response", ".svg");
            FileUtils.writeByteArrayToFile(tempFile, svgFileContent);
            return SVGHelper.fromFile(tempFile);
        } finally {
            if (tempFile != null) {
                FileUtils.forceDelete(tempFile);
            }
        }
    }

    /**
     * Calls rnapdbee-adapters to convert BpSeq to DotBracket.
     *
     * @param bpSeq BpSeq data to convert
     * @return data in DotBracket format
     */
    public DotBracket performBpSeqConversion(BpSeq bpSeq) throws AdaptersErrorException {
        try {
            String adaptersResponse = adaptersWebClient
                    .post()
                    .uri(properties.getBpseqConversionPath())
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(BodyInserters.fromValue(bpSeq.toString()))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> clientResponse
                            .bodyToMono(String.class)
                            .map(resp -> {
                                LOGGER.warn(String.format(ERROR_4XX_GOTTEN_FROM_ADAPTERS_FORMAT,
                                        clientResponse.rawStatusCode(), resp));
                                return new IllegalStateException(String.format(
                                        ERROR_STATUS_GOTTEN_FROM_ADAPTERS_FORMAT, clientResponse.rawStatusCode()));
                            }))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> clientResponse
                            .bodyToMono(String.class)
                            .map(resp -> {
                                LOGGER.warn(String.format(ERROR_5XX_GOTTEN_FROM_ADAPTERS_FORMAT,
                                        clientResponse.rawStatusCode(), resp));
                                return new IllegalStateException(String.format(
                                        ERROR_STATUS_GOTTEN_FROM_ADAPTERS_FORMAT, clientResponse.rawStatusCode()));
                            }))
                    .bodyToMono(String.class)
                    .cache(Duration.ofSeconds(properties.getMonoCacheDurationInSeconds()))
                    .block();
            return ImmutableDefaultDotBracket.fromString(Objects.requireNonNull(adaptersResponse));
        } catch (WebClientException | IllegalStateException exception) {
            LOGGER.error(ERROR_MET_DURING_CALL_TO_ADAPTERS_LOG, exception);
            throw new AdaptersErrorException(ERROR_MET_DURING_CALL_TO_ADAPTERS, exception);
        }
    }

    /**
     * Calls rnapdbee-adapters to ensure the content is a valid mmCIF file.
     *
     * @param fileContent Input in PDB or PDBx/mmCIF format.
     * @return data in PDBx/mmCIF format.
     */
    public String ensureMmCif(String fileContent) throws AdaptersErrorException {
        try {
            return adaptersWebClient
                    .post()
                    .uri(properties.getEnsureMmCifPath())
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(BodyInserters.fromValue(fileContent))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> clientResponse
                            .bodyToMono(String.class)
                            .map(resp -> {
                                LOGGER.warn(String.format(ERROR_4XX_GOTTEN_FROM_ADAPTERS_FORMAT,
                                        clientResponse.rawStatusCode(), resp));
                                return new IllegalStateException(String.format(
                                        ERROR_STATUS_GOTTEN_FROM_ADAPTERS_FORMAT, clientResponse.rawStatusCode()));
                            }))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> clientResponse
                            .bodyToMono(String.class)
                            .map(resp -> {
                                LOGGER.warn(String.format(ERROR_5XX_GOTTEN_FROM_ADAPTERS_FORMAT,
                                        clientResponse.rawStatusCode(), resp));
                                return new IllegalStateException(String.format(
                                        ERROR_STATUS_GOTTEN_FROM_ADAPTERS_FORMAT, clientResponse.rawStatusCode()));
                            }))
                    .bodyToMono(String.class)
                    .cache(Duration.ofSeconds(properties.getMonoCacheDurationInSeconds()))
                    .block();
        } catch (WebClientException | IllegalStateException exception) {
            LOGGER.error(ERROR_MET_DURING_CALL_TO_ADAPTERS_LOG, exception);
            throw new AdaptersErrorException(ERROR_MET_DURING_CALL_TO_ADAPTERS, exception);
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
