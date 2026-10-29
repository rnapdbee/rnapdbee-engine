package pl.poznan.put.rnapdbee.engine.infrastructure.configuration;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.AdaptersAnalysisDTO;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.SaengerType;
import pl.poznan.put.rnapdbee.engine.shared.converter.boundary.ExternalConverter;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Guards the codec configuration of the web client used to call rnapdbee-adapters.
 *
 * <p>Two things are easy to lose during a Spring Boot upgrade and both fail at runtime rather than
 * at start-up: the case-insensitive enum decoding, and the in-memory buffer limit taken from
 * {@code spring.http.codecs.max-in-memory-size}. The latter matters because the adapters client is
 * built with its own {@link org.springframework.web.reactive.function.client.ExchangeStrategies},
 * which replaces the auto-configured codecs.</p>
 */
@SpringBootTest
class AdaptersWebClientCodecTest {

    private static final int RESPONSE_SIZE_BYTES = 600 * 1024;
    private static final String PAYLOAD_PREFIX = "{\"payload\":\"";
    private static final String PAYLOAD_SUFFIX = "\"}";

    @MockitoBean
    ExternalConverter externalConverter;

    @Autowired
    @Qualifier("adaptersWebClient")
    WebClient adaptersWebClient;

    private MockWebServer mockWebServer;
    private WebClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        client = adaptersWebClient.mutate().baseUrl(mockWebServer.url("/").toString()).build();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldDecodeResponseBiggerThanDefaultCodecLimit() {
        String payload = "x".repeat(RESPONSE_SIZE_BYTES);
        enqueueJson(PAYLOAD_PREFIX + payload + PAYLOAD_SUFFIX);

        String response = client.post()
                .uri("/analysis-api/v1/barnaba")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        assertEquals(RESPONSE_SIZE_BYTES,
                response.length() - PAYLOAD_PREFIX.length() - PAYLOAD_SUFFIX.length());
    }

    @Test
    void shouldDecodeEnumValuesIgnoringCase() {
        enqueueJson("""
                {
                  "base_pairs": [
                    {
                      "nt1": {"auth": {"chain": "A", "number": 1, "name": "G"}},
                      "nt2": {"auth": {"chain": "A", "number": 2, "name": "C"}},
                      "saenger": "i"
                    }
                  ]
                }
                """);

        AdaptersAnalysisDTO analysis = client.post()
                .uri("/analysis-api/v1/barnaba")
                .retrieve()
                .bodyToMono(AdaptersAnalysisDTO.class)
                .block();

        assertEquals(1, analysis.getBasePairs().size());
        assertEquals(SaengerType.I, analysis.getBasePairs().get(0).getSaengerType());
    }

    private void enqueueJson(String body) {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(body));
    }
}
