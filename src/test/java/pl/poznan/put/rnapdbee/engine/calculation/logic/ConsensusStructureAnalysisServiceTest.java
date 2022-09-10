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
import pl.poznan.put.rnapdbee.engine.basepair.boundary.MCAnnotateBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.basepair.webclient.AdapterWebClientConfiguration;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.model.ModelSelection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
class ConsensusStructureAnalysisServiceTest {

    static MockWebServer mockWebServer;

    static String EXAMPLE_FILE_PATH_FORMAT = "/3DToMulti2DMocks/%s/pdbfile.pdb";
    static String MC_ANNOTATE_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/mc_annotate_response.json";

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
    void testConsensusAnalysis(String exampleFilename) throws IOException, URISyntaxException {
        prepareMockWebServerStubs(String.format(MC_ANNOTATE_RESPONSE_MOCK_PATH_FORMAT, exampleFilename));
        String fileContent = Files.readString(Paths.get(getClass().getResource(String.format(EXAMPLE_FILE_PATH_FORMAT, exampleFilename)).toURI()));
        var result = cut.analyse(ModelSelection.ALL, true, true, VisualizationTool.VARNA, exampleFilename, fileContent);
        Assertions.assertEquals(result.getEntries().size(), 1);
    }

    private void prepareMockWebServerStubs(String mcAnnotateJsonResponsePath) {
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            @NotNull
            public MockResponse dispatch(RecordedRequest request) {
                if ("/analyze/mc-annotate".equals(request.getPath())) {
                    try {
                        return new MockResponse()
                                .setResponseCode(200)
                                .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                                .setBody(Files.readString(Paths.get(getClass().getResource(mcAnnotateJsonResponsePath).toURI())));
                    } catch (IOException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        mockWebServer.setDispatcher(dispatcher);
    }

    @TestConfiguration
    static class BeansReplacement {
        WebClient mockedWebClient = WebClient.builder()
                .baseUrl(String.format("http://localhost:%s", mockWebServer.getPort()))
                .exchangeStrategies(AdapterWebClientConfiguration.EXCHANGE_STRATEGIES)
                .build();

        @Primary
        @Bean
        MCAnnotateBasePairAnalyzer mockMcAnnotateBasePairAnalyzer() {
            return new MCAnnotateBasePairAnalyzer("/analyze/mc-annotate", mockedWebClient);
        }
    }
}