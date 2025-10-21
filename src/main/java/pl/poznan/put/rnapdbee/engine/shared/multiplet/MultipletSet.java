package pl.poznan.put.rnapdbee.engine.shared.multiplet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.BasePair;
import pl.poznan.put.structure.ClassifiedBasePair;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultipletSet implements Serializable {
    private final List<BaseTriple> baseTriples;

    public MultipletSet(final List<AnalyzedBasePair> basePairs) {
        super();
        baseTriples = MultipletSet.analyze(basePairs);
    }

    public List<BaseTriple> getBaseTriples() {
        return baseTriples;
    }

    private static List<BaseTriple> analyze(final List<? extends AnalyzedBasePair> basePairs) {
        final List<ClassifiedBasePair> filteredBasePairs = basePairs.stream()
                .filter(ClassifiedBasePair::isPairing)
                .flatMap(analyzedBasePair -> Stream.of(analyzedBasePair, analyzedBasePair.invert()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        final Map<PdbNamedResidueIdentifier, List<ClassifiedBasePair>> residueInteractionsMap = new HashMap<>();

        for (final ClassifiedBasePair classifiedBasePair : filteredBasePairs) {
            final BasePair basePair = classifiedBasePair.basePair();
            final PdbNamedResidueIdentifier left = basePair.left();
            if (!residueInteractionsMap.containsKey(left)) {
                residueInteractionsMap.put(left, new ArrayList<>());
            }
            residueInteractionsMap.get(left).add(classifiedBasePair);
        }

        final List<BaseTriple> baseTriples = new ArrayList<>();
        residueInteractionsMap.forEach((key, value) -> {
            if (value.size() == 2) {
                baseTriples.add(new BaseTriple(key, value.get(0), value.get(1)));
            }
        });
        return baseTriples;
    }

    public final List<String> generateMessages() {
        return baseTriples.stream()
                .map(baseTriple -> "Multiplet identified: " + baseTriple)
                .collect(Collectors.toList());
    }

    @Override
    public final String toString() {
        final Object[] array = baseTriples.toArray();
        return new ToStringBuilder(this)
                .append("multiplets", Arrays.toString(array))
                .toString();
    }
}
