package pl.poznan.put.rnapdbee.engine.calculation.testhelp.shared.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.shared.converter.KnotRemoval;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.shared.converter.RNAStructure;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.ImmutableDefaultDotBracket;

@TestConfiguration
public class TestConverterConfiguration {

    @Bean
    @Primary
    public Converter converter() {
        return bpSeq -> {
            RNAStructure structure = new RNAStructure(bpSeq);
            structure = KnotRemoval.dynamicProgrammingOneBest(structure);
            return ImmutableDefaultDotBracket.of(
                    structure.getSequence(), structure.getDotBracketStructure());
        };
    }
}
