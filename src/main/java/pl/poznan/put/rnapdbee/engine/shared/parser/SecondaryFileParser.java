package pl.poznan.put.rnapdbee.engine.shared.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputType;
import pl.poznan.put.rnapdbee.engine.shared.exception.ImproperStructureFormatException;
import pl.poznan.put.rnapdbee.engine.shared.exception.UnknownFileExtensionException;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DefaultDotBracket;
import pl.poznan.put.structure.formats.DotBracket;

@Component
public class SecondaryFileParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecondaryFileParser.class);
    private final Converter converter;

    public DotBracket parseSecondaryFile(String content, InputType inputType, boolean removeIsolated) {
        switch (inputType) {
            case BPSEQ:
                return convertBpSeqIntoDotBracket(content, removeIsolated);
            case CT:
                return convertCtIntoDotBracket(content, removeIsolated);
            case DOT_BRACKET:
                return readDotBracketContent(content, removeIsolated);
            default:
                LOGGER.warn(String.format("non-secondary InputType passed to parseSecondaryFile method: %s",
                        inputType));
                throw new UnknownFileExtensionException(
                        String.format(
                                "Failed attempt to parse secondary structure with input type: %s", inputType));
        }
    }

    private DotBracket convertBpSeqIntoDotBracket(String content, boolean removeIsolated) {
        try {
            BpSeq bpSeq = removeIsolated
                    ? BpSeq.fromString(content).withoutIsolatedPairs()
                    : BpSeq.fromString(content);
            Ct ct = Ct.fromBpSeq(bpSeq);
            return DefaultDotBracket.copyWithStrands(converter.convert(bpSeq), ct);
        } catch (IllegalArgumentException exception) {
            LOGGER.warn(String.format("Error met during parsing of bpseq file with message %s",
                    exception.getMessage()));
            throw new ImproperStructureFormatException("Failed to parse content of the bpseq file", exception);
        }
    }

    private DotBracket convertCtIntoDotBracket(String content, boolean removeIsolated) {
        try {
            Ct ct = removeIsolated
                    ? Ct.fromString(content).withoutIsolatedPairs()
                    : Ct.fromString(content);
            BpSeq bpSeq = BpSeq.fromCt(ct);
            return DefaultDotBracket.copyWithStrands(converter.convert(bpSeq), ct);
        } catch (IllegalArgumentException exception) {
            LOGGER.warn(String.format("Error met during parsing of ct file with message %s",
                    exception.getMessage()));
            throw new ImproperStructureFormatException("Failed to parse content of the ct file", exception);
        }
    }

    private DotBracket readDotBracketContent(String content, boolean removeIsolated) {
        try {
            DotBracket readDotBracket = DefaultDotBracket.fromString(content);
            if (removeIsolated) {
                return DefaultDotBracket.copyWithoutIsolatedBasePairs(readDotBracket);
            } else {
                return readDotBracket;
            }
        } catch (IllegalArgumentException exception) {
            LOGGER.warn(String.format("Error met during parsing of dot bracket file with message %s",
                    exception.getMessage()));
            throw new ImproperStructureFormatException("Failed to parse content of the dot bracket file", exception);
        }
    }

    @Autowired
    public SecondaryFileParser(Converter converter) {
        this.converter = converter;
    }
}
