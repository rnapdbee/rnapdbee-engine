package pl.poznan.put.rnapdbee.engine.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

@Configuration
public class AdapterWebClientConfiguration {

    /**
     * Decoder used for every response coming from rnapdbee-adapters. It accepts enum values
     * regardless of their casing, because adapters do not consistently capitalise their values.
     */
    private static final JsonMapper ADAPTERS_JSON_MAPPER = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .build();

    private final ConnectionProvider provider;
    private final String adaptersBaseUrl;

    @Autowired
    public AdapterWebClientConfiguration(RnaPDBeeAdaptersProperties rnapdbeeAdaptersProperties) {
        this.adaptersBaseUrl = rnapdbeeAdaptersProperties.getAdaptersBaseUrl();
        this.provider = ConnectionProvider.builder("rnapdbee-adapters")
                .maxConnections(rnapdbeeAdaptersProperties.getMaxConnections())
                .maxIdleTime(Duration.ofSeconds(rnapdbeeAdaptersProperties.getMaxIdleTimeSeconds()))
                .maxLifeTime(Duration.ofSeconds(rnapdbeeAdaptersProperties.getMaxLifeTimeSeconds()))
                .pendingAcquireTimeout(Duration.ofSeconds(rnapdbeeAdaptersProperties.getPendingAcquireTimeoutSeconds()))
                .evictInBackground(Duration.ofSeconds(rnapdbeeAdaptersProperties.getEvictInBackgroundSeconds()))
                .build();
    }

    /**
     * Builds exchange strategies for the adapters client so that adapters payloads are decoded with
     * a case-insensitive JSON mapper, independently of the global Jackson settings.
     *
     * <p>Although {@link WebClient.Builder#exchangeStrategies} replaces the auto-configured codecs
     * as a whole, Boot's {@code WebClientCodecCustomizer} runs on the builder afterwards and
     * reinstates the configured {@code spring.http.codecs.max-in-memory-size}. That limit therefore
     * does not have to be duplicated here - see {@code AdaptersWebClientCodecTest} for the guard.</p>
     *
     * @return strategies with the adapters-specific JSON decoder
     */
    @Bean("adaptersExchangeStrategies")
    public ExchangeStrategies adaptersExchangeStrategies() {
        return ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .jacksonJsonDecoder(new JacksonJsonDecoder(ADAPTERS_JSON_MAPPER)))
                .build();
    }

    @Bean("adaptersWebClient")
    public WebClient adaptersWebClient(WebClient.Builder builder,
                                       ExchangeStrategies adaptersExchangeStrategies) {
        return builder
                .exchangeStrategies(adaptersExchangeStrategies)
                .baseUrl(adaptersBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create(provider)))
                .build();
    }
}
