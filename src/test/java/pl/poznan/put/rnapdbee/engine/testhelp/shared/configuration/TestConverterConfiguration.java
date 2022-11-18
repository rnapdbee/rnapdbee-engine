package pl.poznan.put.rnapdbee.engine.testhelp.shared.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import pl.poznan.put.rnapdbee.engine.testhelp.shared.converter.KnotRemoval;
import pl.poznan.put.rnapdbee.engine.testhelp.shared.converter.RNAStructure;
import pl.poznan.put.rnapdbee.engine.shared.converter.MixedIntegerLinearProgrammingConverter;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.ImmutableDefaultDotBracket;

import java.io.IOException;

/**
 * Class replacing bean of {@link MixedIntegerLinearProgrammingConverter} to bean that implements Dynamic Programming
 * approach in Pseudoknot removal problem inside Integration Tests.
 */
@TestConfiguration
public class TestConverterConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(TestConverterConfiguration.class);
    private static final String GUROBI_WORKING = "Gurobi environment and license is set up on this machine. " +
            "Using actual MILP implementation for converting bpseq to dbn format in Integration Tests.";
    private static final String GUROBI_NOT_WORKING = "Gurobi environment or license is not set up on this machine. " +
            "Using DP_NEW implementation for converting bpseq to dbn format in Integration Tests.";

    @Bean
    @Primary
    public Converter converter() {
        boolean isGurobiUp = checkIfGurobiIsSetUpCorrectly();
        if (isGurobiUp) {
            logger.info(GUROBI_WORKING);
            return new MixedIntegerLinearProgrammingConverter(logger);
        } else {
            logger.info(GUROBI_NOT_WORKING);
            return bpSeq -> {
                RNAStructure structure = new RNAStructure(bpSeq);
                structure = KnotRemoval.dynamicProgrammingOneBest(structure);
                return ImmutableDefaultDotBracket.of(
                        structure.getSequence(), structure.getDotBracketStructure());
            };
        }
    }

    public static boolean checkIfGurobiIsSetUpCorrectly() {
        Runtime rt = Runtime.getRuntime();
        Process pr;
        try {
            pr = rt.exec("gurobi_cl");
        } catch (IOException e) {
            return false;
        }
        int statusCode;
        try {
            statusCode = pr.waitFor();
        } catch (InterruptedException e) {
            return false;
        }
        return statusCode == 0;
    }
}
