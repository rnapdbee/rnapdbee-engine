package pl.poznan.put.rnapdbee.engine.calculation.tertiary.validator;

import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueCollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class RNAValidator {
  // value taken from RNAView
  private static final double C2_C6_MAX_DISTANCE = 3.0;
  // value taken from RNAView
  private static final double N1_C6_MAX_DISTANCE = 2.0;
  // value taken from RNAView
  private static final double N1_C2_MAX_DISTANCE = 2.0;
  private final Templates templates;

  public RNAValidator(final Templates templates) {
    super();
    this.templates = templates;
  }

  public static boolean isCorrectAccordingToRNAView(final PdbResidue residue) {
    if (Stream.of(AtomName.C2, AtomName.C6, AtomName.N1).allMatch(residue::hasAtom)) {
      final PdbAtomLine c2Atom = residue.findAtom(AtomName.C2);
      final PdbAtomLine c6Atom = residue.findAtom(AtomName.C6);
      final PdbAtomLine n1Atom = residue.findAtom(AtomName.N1);

      final Vector<Euclidean3D> c2 = new Vector3D(c2Atom.x(), c2Atom.y(), c2Atom.z());
      final Vector<Euclidean3D> c6 = new Vector3D(c6Atom.x(), c6Atom.y(), c6Atom.z());
      final Vector<Euclidean3D> n1 = new Vector3D(n1Atom.x(), n1Atom.y(), n1Atom.z());

      final double d1 = c2.distance(c6);
      final double d2 = n1.distance(c6);
      final double d3 = n1.distance(c2);

      return d1 <= RNAValidator.C2_C6_MAX_DISTANCE
              && d2 <= RNAValidator.N1_C6_MAX_DISTANCE
              && d3 <= RNAValidator.N1_C2_MAX_DISTANCE;
    }

    return false;
  }

  public final List<String> validate(final ResidueCollection model) {
    final List<String> messages = new ArrayList<>();
    for (final PdbResidue residue : model.residues()) {
      validateResidue(residue, messages);
    }
    return messages;
  }

  private void validateResidue(
          final PdbResidue residue, final Collection<? super String> messages) {
    final String residueName = residue.modifiedResidueName();

    if (!templates.isProperResidueName(residueName)) {
      messages.add(
              "Invalid residue name for: "
                      + residue
                      + ". Valid names: "
                      + templates.getProperResidueNames());
      return;
    }

    for (final PdbAtomLine atom : residue.atoms()) {
      final String atomName = atom.atomName();

      if (!templates.isProperAtomName(residueName, atomName)) {
        messages.add("Invalid atom name in residue " + residue + ": " + atomName);
        return;
      }
    }

    final boolean isCorrect = RNAValidator.isCorrectAccordingToRNAView(residue);

    if (!isCorrect) {
      final String originalResidueName = residue.standardResidueName();

      if (Arrays.asList("A", "C", "G", "U").contains(originalResidueName)) {
        messages.add("Warning! The residue " + residue + " may be invalid w.r.t. C2/C6/N1 atoms");
        return;
      }

      messages.add("Invalid residue: " + residue);
    }
  }
}
