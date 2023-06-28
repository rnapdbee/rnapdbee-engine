package pl.poznan.put.rnapdbee.engine.shared.converter.boundary;


import gurobi.GRBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.poznan.put.rnapdbee.engine.testhelp.SkipIfGurobiIsNotAvailable;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.DefaultDotBracket;
import pl.poznan.put.structure.formats.DotBracket;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MixedIntegerLinearProgrammingConverterTest {

    @InjectMocks
    private MixedIntegerLinearProgrammingConverter mixedIntegerLinearProgrammingConverter;

    @BeforeEach
    void setUp() throws GRBException {
        mixedIntegerLinearProgrammingConverter.initializeGurobiEnvironment();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/MILPConverterTestCases.csv", maxCharsPerColumn = 65536)
    @SkipIfGurobiIsNotAvailable
    public void shouldConvertBpSeqCorrectly(
            String modelName,
            String inputBpSeq,
            String outputDotBracket) {
        // given
        BpSeq bpSeq = BpSeq.fromString(inputBpSeq);
        DotBracket expected = DefaultDotBracket.fromString(outputDotBracket);
        // when
        DotBracket actual = mixedIntegerLinearProgrammingConverter.convert(bpSeq);
        // then
        assertEquals(expected, actual,
                String.format("Output dotBracket does not match expected dotBracket for modelName: %s", modelName));
    }
}
