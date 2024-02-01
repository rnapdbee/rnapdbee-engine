package pl.poznan.put.rnapdbee.engine.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.poznan.put.rnapdbee.engine.shared.converter.boundary.ExternalConverter;
import pl.poznan.put.structure.formats.Converter;

@Configuration
public class ConverterConfiguration {

    private final ExternalConverter converter;

    public ConverterConfiguration(ExternalConverter converter) {
        this.converter = converter;
    }

    @Bean
    public Converter converter() {
        return converter;
    }
}
