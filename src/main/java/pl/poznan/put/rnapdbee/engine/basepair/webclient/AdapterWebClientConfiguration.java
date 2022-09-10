package pl.poznan.put.rnapdbee.engine.basepair.webclient;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AdapterWebClientConfiguration {

    final static ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .build();
    public static final ExchangeStrategies EXCHANGE_STRATEGIES = ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs()
                    .jackson2JsonDecoder(new Jackson2JsonDecoder(OBJECT_MAPPER)))
            .build();

    @Value("${rnapdbee.adapters.global.host}")
    private String adaptersBaseUrl;

    @Bean("adaptersWebClient")
    public WebClient adaptersWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .exchangeStrategies(EXCHANGE_STRATEGIES)
                .baseUrl(adaptersBaseUrl)
                .build();
    }
}
