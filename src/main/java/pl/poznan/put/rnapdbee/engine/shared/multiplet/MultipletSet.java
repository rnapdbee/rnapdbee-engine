package pl.poznan.put.rnapdbee.engine.shared.multiplet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.BasePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultipletSet implements Serializable {
    private final Collection<Multiplet> multiplets;

    public MultipletSet(final Iterable<AnalyzedBasePair> basePairs) {
        super();
        multiplets = MultipletSet.analyze(basePairs);
    }

    private static Collection<Multiplet> analyze(
            final Iterable<? extends AnalyzedBasePair> basePairs) {
        final Map<PdbNamedResidueIdentifier, List<PdbNamedResidueIdentifier>> interactionMap =
                new HashMap<>();
        for (final AnalyzedBasePair cbp : basePairs) {
            final BasePair basePair = cbp.basePair();
            final PdbNamedResidueIdentifier left = basePair.left();
            final PdbNamedResidueIdentifier right = basePair.right();

            if (!interactionMap.containsKey(left)) {
                interactionMap.put(left, new ArrayList<>());
            }
            if (!interactionMap.containsKey(right)) {
                interactionMap.put(right, new ArrayList<>());
            }

            interactionMap.get(left).add(right);
            interactionMap.get(right).add(left);
        }

        final Collection<Multiplet> multiplets = new ArrayList<>();
        for (final Map.Entry<PdbNamedResidueIdentifier, List<PdbNamedResidueIdentifier>> entry :
                interactionMap.entrySet()) {
            final PdbNamedResidueIdentifier identifier = entry.getKey();
            final List<PdbNamedResidueIdentifier> interactions = entry.getValue();

            if (interactions.size() > 1) {
                multiplets.add(new Multiplet(identifier, interactions));
            }
        }

        return multiplets;
    }

    public final List<String> generateMessages() {
        return multiplets.stream()
                .map(multiplet -> "Multiplet identified: " + multiplet)
                .collect(Collectors.toList());
    }

    @Override
    public final String toString() {
        final Object[] array = multiplets.toArray();
        return new ToStringBuilder(this).append("multiplets", Arrays.toString(array)).toString();
    }
}
