package pl.poznan.put.rnapdbee.engine.shared.elements;

import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.ImmutableDefaultPdbModel;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueCollection;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.formats.DotBracketFromPdb;
import pl.poznan.put.structure.formats.Strand;

import java.util.ArrayList;
import java.util.List;

public class StructuralElementFromPdb extends StructuralElement {
    private final DotBracketFromPdb dotBracket;

    private PdbModel structureModel;

    protected StructuralElementFromPdb(
            final DotBracketFromPdb dotBracket, final List<Strand> strands) {
        super(dotBracket, strands);
        this.dotBracket = dotBracket;
    }

    protected StructuralElementFromPdb(final DotBracketFromPdb dotBracket, final Strand... strands) {
        super(dotBracket, strands);
        this.dotBracket = dotBracket;
    }

    public final PdbModel apply(final ResidueCollection wholeStructure) throws PdbParsingException {
        // lazy evaluation
        if (structureModel == null) {
            final List<PdbAtomLine> atoms = new ArrayList<>();

            for (final Strand strand : strands) {
                for (int i = strand.begin(); i < strand.end(); i++) {
                    final DotBracketSymbol symbol = dotBracket.symbols().get(i);
                    final PdbResidueIdentifier identifier = dotBracket.identifier(symbol);
                    final PdbResidue residue = wholeStructure.findResidue(identifier);
                    atoms.addAll(residue.atoms());
                }
            }

            structureModel = ImmutableDefaultPdbModel.of(atoms);
        }

        return structureModel;
    }
}
