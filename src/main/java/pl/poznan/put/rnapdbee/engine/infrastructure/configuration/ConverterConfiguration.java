package pl.poznan.put.rnapdbee.engine.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.poznan.put.rnapdbee.engine.shared.converter.MixedIntegerLinearProgrammingConverter;
import pl.poznan.put.structure.formats.Converter;

@Configuration
public class ConverterConfiguration {

    @Bean
    public Converter converter() {
        return new MixedIntegerLinearProgrammingConverter();
    }
}
