package pl.poznan.put.rnapdbee.engine.calculation.logic;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.engine.basepair.boundary.BPNetBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.basepair.boundary.BarnabaBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.basepair.boundary.MCAnnotateBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.basepair.boundary.RnaViewBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.basepair.webclient.AdapterWebClientConfiguration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Abstract testing Class mocking the calls to rnapdbee-adapters thanks to {@link MockWebServer}.
 * Overrides {@link pl.poznan.put.rnapdbee.engine.basepair.boundary.RNApdbeeAdapterBasePairAnalyzer} beans with beans that
 * depends on state of mockWebServer. Cleanup of those beans is assured by usage of {@link DirtiesContext} annotation.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractTertiaryStructureAnalysisTestingClass {

    protected String EXAMPLE_PDB_FILE_PATH_FORMAT;
    protected String EXAMPLE_CIF_FILE_PATH_FORMAT;

    protected String BARNABA_RESPONSE_MOCK_PATH_FORMAT;
    protected String BPNET_RESPONSE_MOCK_PATH_FORMAT;
    protected String MC_ANNOTATE_RESPONSE_MOCK_PATH_FORMAT;
    protected String RNAVIEW_RESPONSE_MOCK_PATH_FORMAT;

    protected String readFileContentFromFile(String exampleFilename) {
        if (exampleFilename.contains(".pdb")) {
            return readFileAsString(String.format(EXAMPLE_PDB_FILE_PATH_FORMAT, exampleFilename));
        } else if (exampleFilename.contains(".cif")) {
            return readFileAsString(String.format(EXAMPLE_CIF_FILE_PATH_FORMAT, exampleFilename));
        }
        throw new IllegalArgumentException("example file name is neither pdb, nor cif");
    }

    protected static MockWebServer mockWebServer;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(0);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    protected void prepareMockWebServerStubs(String exampleFileName) {
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            @NotNull
            public MockResponse dispatch(RecordedRequest request) {
                if ("/analyze/barnaba".equals(request.getPath())) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                            .setBody(readFileAsString(String.format(BARNABA_RESPONSE_MOCK_PATH_FORMAT, exampleFileName)));
                }
                if ("/analyze/bpnet".equals(request.getPath())) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                            .setBody(readFileAsString(String.format(BPNET_RESPONSE_MOCK_PATH_FORMAT, exampleFileName)));
                }
                if ("/analyze/mc-annotate".equals(request.getPath())) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                            .setBody(readFileAsString(String.format(MC_ANNOTATE_RESPONSE_MOCK_PATH_FORMAT, exampleFileName)));
                }
                if ("/analyze/rnaview".equals(request.getPath())) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                            .setBody(readFileAsString(String.format(RNAVIEW_RESPONSE_MOCK_PATH_FORMAT, exampleFileName)));
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        mockWebServer.setDispatcher(dispatcher);
    }

    protected String readFileAsString(String pathToFile) {
        try {
            return Files.readString(Paths.get(Objects.requireNonNull(getClass().getResource(pathToFile)).toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @TestConfiguration
    public static class BeansReplacement {
        Supplier<WebClient> mockedWebClientSupplier = () -> WebClient.builder()
                .baseUrl(String.format("http://localhost:%s", mockWebServer.getPort()))
                .exchangeStrategies(AdapterWebClientConfiguration.EXCHANGE_STRATEGIES)
                .build();

        @Primary
        @Bean
        BarnabaBasePairAnalyzer mockBarnabaBasePairAnalyzer() {
            return new BarnabaBasePairAnalyzer("/analyze/barnaba", mockedWebClientSupplier.get());
        }

        @Primary
        @Bean
        BPNetBasePairAnalyzer mockBPNetBasePairAnalyzer() {
            return new BPNetBasePairAnalyzer("/analyze/bpnet", mockedWebClientSupplier.get());
        }

        @Primary
        @Bean
        MCAnnotateBasePairAnalyzer mockMcAnnotateBasePairAnalyzer() {
            return new MCAnnotateBasePairAnalyzer("/analyze/mc-annotate", mockedWebClientSupplier.get());
        }

        @Primary
        @Bean
        RnaViewBasePairAnalyzer mockRnaViewBasePairAnalyzer() {
            return new RnaViewBasePairAnalyzer("/analyze/rnaview", mockedWebClientSupplier.get());
        }
    }
}
