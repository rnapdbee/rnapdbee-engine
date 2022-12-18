package pl.poznan.put.rnapdbee.engine.infrastructure.configuration;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class AdapterWebClientConfiguration {

    public static final ExchangeStrategies EXCHANGE_STRATEGIES = ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs()
                    .jackson2JsonDecoder(new Jackson2JsonDecoder(JsonMapper.builder()
                            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                            .build())))
            .build();

    private final ConnectionProvider provider;
    private final String adaptersBaseUrl;

    @Bean("adaptersWebClient")
    @Autowired
    public WebClient adaptersWebClient(WebClient.Builder builder) {
        return builder
                .exchangeStrategies(EXCHANGE_STRATEGIES)
                .baseUrl(adaptersBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create(provider)))
                .build();
    }

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
}
