package pl.poznan.put.rnapdbee.engine.shared.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputType;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.DefaultDotBracket;
import pl.poznan.put.structure.formats.DotBracket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecondaryFileParserTest {

    @Mock
    private Converter converter;

    @InjectMocks
    private SecondaryFileParser secondaryFileParser;

    @Test
    void shouldParseBpSeqWithRedundantSpacesAndEmptyLines() {
        String content = "1 A 4\n2   U 3\n\n 3 G  2\n4 C   1\n";
        DotBracket conversionResult = mock(DotBracket.class);
        when(conversionResult.sequence()).thenReturn("AAAA");
        when(conversionResult.structure()).thenReturn("()()");
        when(converter.convert(any(BpSeq.class))).thenReturn(conversionResult);

        DotBracket result = secondaryFileParser.parseSecondaryFile(content, InputType.BPSEQ, false);

        assertThat(result).isNotNull();
        verify(converter).convert(any(BpSeq.class));
    }

    @Test
    void shouldParseCtWithRedundantSpacesAndEmptyLines() {
        String content = " 4   some header\n1 A 0 2 4 1\n 2 U 1 3 3 2\n\n3 G 2 4 2 3\n4 C 3 0 1 4\n";
        DotBracket conversionResult = mock(DotBracket.class);
        when(conversionResult.sequence()).thenReturn("AAAA");
        when(conversionResult.structure()).thenReturn("()()");
        when(converter.convert(any(BpSeq.class))).thenReturn(conversionResult);

        DotBracket result = secondaryFileParser.parseSecondaryFile(content, InputType.CT, false);

        assertThat(result).isNotNull();
        verify(converter).convert(any(BpSeq.class));
    }
}
