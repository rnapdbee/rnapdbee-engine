package pl.poznan.put.rnapdbee.engine.infrastructure.configuration;

import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.poznan.put.rnapdbee.engine.shared.converter.boundary.MixedIntegerLinearProgrammingConverter;
import pl.poznan.put.structure.formats.Converter;

@Configuration
public class ConverterConfiguration {

    @Bean
    public Converter converter(Logger logger) {
        return new MixedIntegerLinearProgrammingConverter(logger);
    }
}
