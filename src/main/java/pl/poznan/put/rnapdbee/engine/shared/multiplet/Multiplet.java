package pl.poznan.put.rnapdbee.engine.shared.multiplet;

import pl.poznan.put.pdb.PdbNamedResidueIdentifier;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

public class Multiplet implements Serializable {
    private final PdbNamedResidueIdentifier identifier;
    private final Collection<PdbNamedResidueIdentifier> interactions;

    public Multiplet(
            final PdbNamedResidueIdentifier identifier,
            final List<PdbNamedResidueIdentifier> interactions) {
        super();
        this.identifier = identifier;
        this.interactions = new TreeSet<>(interactions);
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        final Multiplet multiplet = (Multiplet) obj;
        return Objects.equals(identifier, multiplet.identifier)
                && Objects.equals(interactions, multiplet.interactions);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(identifier, interactions);
    }

    @Override
    public final String toString() {
        return identifier + " -> " + Arrays.toString(interactions.toArray());
    }
}
