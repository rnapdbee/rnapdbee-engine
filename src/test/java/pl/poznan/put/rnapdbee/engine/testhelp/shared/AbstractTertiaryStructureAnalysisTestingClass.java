package pl.poznan.put.rnapdbee.engine.testhelp.shared;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.WeblogoConsensualVisualizationDrawer;
import pl.poznan.put.rnapdbee.engine.infrastructure.configuration.RnaPDBeeAdaptersProperties;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.BPNetBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.BarnabaBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.MCAnnotateBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.BasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.RnaViewBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.RnapolisBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.infrastructure.configuration.AdapterWebClientConfiguration;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.component.PathDeterminer;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RnaPDBeeAdaptersCaller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Abstract testing Class mocking the calls to rnapdbee-adapters thanks to {@link MockWebServer}.
 * Overrides {@link BasePairAnalyzer} beans with beans that
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
    protected String RNAPOLIS_RESPONSE_MOCK_PATH_FORMAT;

    private final String MOCKED_WEBLOGO_RESPONSE = "mock";

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
                if ("/analysis-api/v1/barnaba".equals(request.getPath())) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                            .setBody(readFileAsString(String.format(BARNABA_RESPONSE_MOCK_PATH_FORMAT, exampleFileName)));
                }
                if ("/analysis-api/v1/bpnet".equals(request.getPath())) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                            .setBody(readFileAsString(String.format(BPNET_RESPONSE_MOCK_PATH_FORMAT, exampleFileName)));
                }
                if ("/analysis-api/v1/mc-annotate".equals(request.getPath())) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                            .setBody(readFileAsString(String.format(MC_ANNOTATE_RESPONSE_MOCK_PATH_FORMAT, exampleFileName)));
                }
                if ("/analysis-api/v1/rnaview".equals(request.getPath())) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                            .setBody(readFileAsString(String.format(RNAVIEW_RESPONSE_MOCK_PATH_FORMAT, exampleFileName)));
                }
                if ("/analysis-api/v1/rnapolis".equals(request.getPath())) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                            .setBody(readFileAsString(String.format(RNAPOLIS_RESPONSE_MOCK_PATH_FORMAT, exampleFileName)));
                }
                if ("/visualization-api/v1/weblogo".equals(request.getPath())) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .addHeader("Content-Type", "image/svg+xml")
                            .setBody(MOCKED_WEBLOGO_RESPONSE);
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        mockWebServer.setDispatcher(dispatcher);
    }

    protected String readFileAsString(String pathToFile) {
        try {
            return Files.readString(Paths.get(Objects.requireNonNull(getClass().getResource(pathToFile)).toURI()));
        } catch (IOException | URISyntaxException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    @TestConfiguration
    public static class BeansReplacement {

        @Autowired
        RnaPDBeeAdaptersProperties rnapdbeeAdaptersProperties;
        @Autowired
        PathDeterminer pathDeterminer;

        Supplier<WebClient> mockedWebClientSupplier = () -> WebClient.builder()
                .baseUrl(String.format("http://localhost:%s", mockWebServer.getPort()))
                .exchangeStrategies(AdapterWebClientConfiguration.EXCHANGE_STRATEGIES)
                .build();

        Supplier<RnaPDBeeAdaptersCaller> mockedRnapdbeeAdaptersCallerSupplier =
                () -> new RnaPDBeeAdaptersCaller(rnapdbeeAdaptersProperties, mockedWebClientSupplier.get(),
                        pathDeterminer);

        @Primary
        @Bean
        BarnabaBasePairAnalyzer mockBarnabaBasePairAnalyzer() {
            return new BarnabaBasePairAnalyzer(mockedRnapdbeeAdaptersCallerSupplier.get());
        }

        @Primary
        @Bean
        BPNetBasePairAnalyzer mockBPNetBasePairAnalyzer() {
            return new BPNetBasePairAnalyzer(mockedRnapdbeeAdaptersCallerSupplier.get());
        }

        @Primary
        @Bean
        MCAnnotateBasePairAnalyzer mockMcAnnotateBasePairAnalyzer() {
            return new MCAnnotateBasePairAnalyzer(mockedRnapdbeeAdaptersCallerSupplier.get());
        }

        @Primary
        @Bean
        RnaViewBasePairAnalyzer mockRnaViewBasePairAnalyzer() {
            return new RnaViewBasePairAnalyzer(mockedRnapdbeeAdaptersCallerSupplier.get());
        }

        @Primary
        @Bean
        RnapolisBasePairAnalyzer mockRnapolisBasePairAnalyzer() {
            return new RnapolisBasePairAnalyzer(mockedRnapdbeeAdaptersCallerSupplier.get());
        }

        @Primary
        @Bean
        WeblogoConsensualVisualizationDrawer mockWeblogoConsensualVisualizationDrawer() {
            return new WeblogoConsensualVisualizationDrawer(mockedRnapdbeeAdaptersCallerSupplier.get());
        }
    }
}
