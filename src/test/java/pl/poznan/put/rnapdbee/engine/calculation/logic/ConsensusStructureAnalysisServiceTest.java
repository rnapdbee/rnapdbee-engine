package pl.poznan.put.rnapdbee.engine.calculation.logic;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.engine.basepair.boundary.BPNetBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.basepair.boundary.BarnabaBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.basepair.boundary.MCAnnotateBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.basepair.boundary.RnaViewBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.basepair.webclient.AdapterWebClientConfiguration;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.model.ModelSelection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@SpringBootTest
class ConsensusStructureAnalysisServiceTest {

    static MockWebServer mockWebServer;

    static String EXAMPLE_FILE_PATH_FORMAT = "/3DToMulti2DMocks/%s/pdbfile.pdb";
    static String BARNABA_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/barnaba_response.json";
    static String BPNET_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/bpnet_response.json";
    static String MC_ANNOTATE_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/mc_annotate_response.json";
    static String RNAVIEW_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/rnaview_response.json";

    @Autowired
    @InjectMocks
    ConsensusStructureAnalysisService cut;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/3dToMulti2DTestCases.csv")
    @Timeout(60)
    void testConsensusAnalysis(String exampleFilename) {
        prepareMockWebServerStubs(exampleFilename);
        String fileContent = readFileAsString(String.format(EXAMPLE_FILE_PATH_FORMAT, exampleFilename));
        // TODO: make all enums configurable via csv file.
        var result = cut.analyse(ModelSelection.ALL, true, true, VisualizationTool.VARNA, exampleFilename, fileContent);
        // TODO: add more assertions.
        Assertions.assertEquals(result.getEntries().size(), 4);
    }

    private void prepareMockWebServerStubs(String exampleFileName) {
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

    private String readFileAsString(String pathToFile) {
        try {
            return Files.readString(Paths.get(Objects.requireNonNull(getClass().getResource(pathToFile)).toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @TestConfiguration
    static class BeansReplacement {
        WebClient mockedWebClient = WebClient.builder()
                .baseUrl(String.format("http://localhost:%s", mockWebServer.getPort()))
                .exchangeStrategies(AdapterWebClientConfiguration.EXCHANGE_STRATEGIES)
                .build();

        @Primary
        @Bean
        BarnabaBasePairAnalyzer mockBarnabaBasePairAnalyzer() {
            return new BarnabaBasePairAnalyzer("/analyze/barnaba", mockedWebClient);
        }

        @Primary
        @Bean
        BPNetBasePairAnalyzer mockBPNetBasePairAnalyzer() {
            return new BPNetBasePairAnalyzer("/analyze/bpnet", mockedWebClient);
        }

        @Primary
        @Bean
        MCAnnotateBasePairAnalyzer mockMcAnnotateBasePairAnalyzer() {
            return new MCAnnotateBasePairAnalyzer("/analyze/mc-annotate", mockedWebClient);
        }

        @Primary
        @Bean
        RnaViewBasePairAnalyzer mockRnaViewBasePairAnalyzer() {
            return new RnaViewBasePairAnalyzer("/analyze/rnaview", mockedWebClient);
        }
    }
}