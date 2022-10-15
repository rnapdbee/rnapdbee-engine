package pl.poznan.put.rnapdbee.engine.calculation.logic;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
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
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.consensual.ConsensualAnalysisTestInformation;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.consensual.ConsensualAnalysisTestInformationAggregator;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.consensual.ConsensualAnalysisTestUtils;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.model.ModelSelection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@SpringBootTest
class ConsensualStructureAnalysisServiceTest {

    static MockWebServer mockWebServer;

    static String EXAMPLE_PDB_FILE_PATH_FORMAT = "/3DToMulti2DMocks/%s/pdbfile.pdb";
    static String EXAMPLE_CIF_FILE_PATH_FORMAT = "/3DToMulti2DMocks/%s/mmciffile.cif";

    static String BARNABA_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/barnaba_response.json";
    static String BPNET_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/bpnet_response.json";
    static String MC_ANNOTATE_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/mc_annotate_response.json";
    static String RNAVIEW_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/rnaview_response.json";

    @Autowired
    @InjectMocks
    ConsensualStructureAnalysisService cut;

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
    void testConsensualAnalysis(String exampleFilename, ModelSelection modelSelection, boolean includeNonCanonical,
                                boolean removeIsolated, VisualizationTool visualizationTool,
                                @AggregateWith(ConsensualAnalysisTestInformationAggregator.class)
                               List<ConsensualAnalysisTestInformation> expectedInformationList) {
        prepareMockWebServerStubs(exampleFilename);
        String fileContent = readFileContentFromFile(exampleFilename);
        var result = cut.analyse(modelSelection, includeNonCanonical, removeIsolated, visualizationTool, exampleFilename, fileContent);
        // TODO: add assertions for adapterEnums when rnapdbee-common code is merged with rnapdbee-engine
        ConsensualAnalysisTestUtils.assertAnalysisOutput(result, expectedInformationList);
    }

    private String readFileContentFromFile(String exampleFilename) {
        if (exampleFilename.contains(".pdb")) {
            return readFileAsString(String.format(EXAMPLE_PDB_FILE_PATH_FORMAT, exampleFilename));
        } else if (exampleFilename.contains(".cif")) {
            return readFileAsString(String.format(EXAMPLE_CIF_FILE_PATH_FORMAT, exampleFilename));
        }
        throw new IllegalArgumentException("example file name is neither pdb, nor cif");
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
