package pl.poznan.put.rnapdbee.engine.shared.parser;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import pl.poznan.put.pdb.analysis.CifParser;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbParser;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputType;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

@Component
public class TertiaryFileParser {

    // TODO: Are these classes are stateless?
    private final CifParser cifParser = new CifParser();
    private final PdbParser pdbParser = new PdbParser(false);

    @Nonnull
    @Cacheable("fileContents")
    public List<? extends PdbModel> parseFileContents(
            final InputType inputType, final String fileContents) throws IOException {
        switch (inputType) {
            case PDB:
                return pdbParser.parse(fileContents);
            case MMCIF:
                return cifParser.parse(fileContents);
            case DOT_BRACKET:
            case BPSEQ:
            case CT:
            default:
                throw new IllegalArgumentException(
                        String.format(
                                "Failed attempt to analyze tertiary structure with input type: %s", inputType));
        }
    }
}
