package pl.poznan.put.rnapdbee.engine.shared.converter.boundary;


import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.poznan.put.rnapdbee.engine.testhelp.shared.converter.KnotRemoval;
import pl.poznan.put.rnapdbee.engine.testhelp.shared.converter.RNAStructure;
import pl.poznan.put.structure.formats.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MixedIntegerLinearProgrammingConverterTest {
    private Converter converter = bpSeq -> {
        RNAStructure structure = new RNAStructure(bpSeq);
        structure = KnotRemoval.dynamicProgrammingOneBest(structure);
        return ImmutableDefaultDotBracket.of(
                structure.getSequence(), structure.getDotBracketStructure());
    };

    @ParameterizedTest
    @CsvFileSource(resources = "/MILPConverterTestCases.csv", maxCharsPerColumn = 65536)
    public void shouldConvertBpSeqCorrectly(
            String modelName,
            String inputBpSeq,
            String outputDotBracket) {
        // given
        BpSeq bpSeq = BpSeq.fromString(inputBpSeq);
        DotBracket expected = DefaultDotBracket.fromString(outputDotBracket);
        // when
        DotBracket actual = converter.convert(bpSeq);
        // then
        assertEquals(expected, actual,
                String.format("Output dotBracket does not match expected dotBracket for modelName: %s", modelName));
    }
}
