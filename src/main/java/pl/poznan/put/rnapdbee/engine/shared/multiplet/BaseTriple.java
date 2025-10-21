package pl.poznan.put.rnapdbee.engine.shared.multiplet;

import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.NucleobaseEdge;
import pl.poznan.put.notation.Stericity;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.ClassifiedBasePair;

import java.io.Serializable;
import java.util.*;

public class BaseTriple implements Serializable {
    private final PdbNamedResidueIdentifier identifier;
    private final ClassifiedBasePair firstBasePair;
    private final ClassifiedBasePair secondBasePair;

    public BaseTriple(
            final PdbNamedResidueIdentifier identifier,
            final ClassifiedBasePair firstBasePair,
            final ClassifiedBasePair secondBasePair) {
        this.identifier = identifier;

        if (BaseTriple.leontisWesthofToScore(firstBasePair.leontisWesthof())
                < BaseTriple.leontisWesthofToScore(secondBasePair.leontisWesthof())) {
            this.firstBasePair = firstBasePair;
            this.secondBasePair = secondBasePair;
        } else {
            this.firstBasePair = secondBasePair;
            this.secondBasePair = firstBasePair;
        }
    }

    public PdbNamedResidueIdentifier getIdentifier() {
        return identifier;
    }

    public ClassifiedBasePair getFirstBasePair() {
        return firstBasePair;
    }

    public ClassifiedBasePair getSecondBasePair() {
        return secondBasePair;
    }

    public String type() {
        return firstBasePair.leontisWesthof().shortName() + "/"
                + secondBasePair.leontisWesthof().shortName();
    }

    /**
     * Return a score for sorting Leontis-Westhof classification. The value for cis/trans is 0 or 100. The value for WC edge is 0, for Hoogsteen edge is 10 or 1 and for Sugar is 20 or 2, respectively for 5' and 3' edges.
     *
     * @param leontisWesthof The classification to compute score for .
     * @return The numeric value.
     */
    private static int leontisWesthofToScore(final LeontisWesthof leontisWesthof) {
        int score = 0;
        score += leontisWesthof.stericity() == Stericity.CIS ? 0 : 100;
        score += leontisWesthof.edge5() == NucleobaseEdge.WATSON_CRICK
                ? 0
                : (leontisWesthof.edge5() == NucleobaseEdge.HOOGSTEEN ? 10 : 20);
        score += leontisWesthof.edge3() == NucleobaseEdge.WATSON_CRICK
                ? 0
                : (leontisWesthof.edge3() == NucleobaseEdge.HOOGSTEEN ? 1 : 2);
        return score;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        final BaseTriple baseTriple = (BaseTriple) obj;
        return Objects.equals(identifier, baseTriple.identifier)
                && Objects.equals(firstBasePair, baseTriple.firstBasePair)
                && Objects.equals(secondBasePair, baseTriple.secondBasePair);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(identifier, firstBasePair, secondBasePair);
    }

    @Override
    public final String toString() {
        return identifier + " " + firstBasePair.leontisWesthof().shortName() + "/"
                + secondBasePair.leontisWesthof().shortName() + " "
                + firstBasePair.basePair().right() + " "
                + secondBasePair.basePair().right();
    }
}
