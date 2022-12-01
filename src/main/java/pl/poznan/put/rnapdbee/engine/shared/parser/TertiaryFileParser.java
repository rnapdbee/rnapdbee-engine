package pl.poznan.put.rnapdbee.engine.shared.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import pl.poznan.put.pdb.analysis.CifParser;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbParser;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputType;
import pl.poznan.put.rnapdbee.engine.shared.exception.UnknownFileExtensionException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

@Component
public class TertiaryFileParser {

    private final Logger logger = LoggerFactory.getLogger(TertiaryFileParser.class);

    // TODO: Are these classes are stateless?
    private final CifParser cifParser = new CifParser();
    private final PdbParser pdbParser = new PdbParser(false);

    @Nonnull
    @Cacheable("FileContents")
    public List<? extends PdbModel> parseFileContents(final InputType inputType, final String fileContents) {
        switch (inputType) {
            case PDB:
                return pdbParser.parse(fileContents);
            case MMCIF:
                try {
                    return cifParser.parse(fileContents);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            case DOT_BRACKET:
            case BPSEQ:
            case CT:
            default:
                logger.warn(String.format("non-tertiary InputType passed to parseSecondaryFile method: %s", inputType));
                throw new UnknownFileExtensionException(
                        String.format(
                                "Failed attempt to parse tertiary structure with input type: %s", inputType));
        }
    }
}
