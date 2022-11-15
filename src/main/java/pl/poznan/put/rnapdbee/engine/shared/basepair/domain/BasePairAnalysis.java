package pl.poznan.put.rnapdbee.engine.shared.basepair.domain;

import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.DefaultPdbModel;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.BasePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class containing result of base pair analysis.
 */
public class BasePairAnalysis {

    private final List<AnalyzedBasePair> represented;
    private final List<AnalyzedBasePair> canonical;
    private final List<AnalyzedBasePair> nonCanonical;
    private final List<AnalyzedBasePair> stacking;
    private final List<AnalyzedBasePair> basePhosphate;
    private final List<AnalyzedBasePair> baseRibose;
    private final List<AnalyzedBasePair> other;
    private final List<AnalyzedBasePair> interStrand;
    private final List<String> messages;

    /**
     * Scan provided base pairs to find isolated ones. Here, isolated means that the nucleotides
     * involved (a) do not form multiplets and (b) their predecessor and successor nucleotides are
     * without any pair.
     *
     * @param model     An instance of {@link DefaultPdbModel} required to get unique indices for
     *                  residues.
     * @param basePairs An {@link Iterable} of base pairs among which the search for isolated ones is
     */
    private static Collection<AnalyzedBasePair> findIsolatedBasePairs(
            final PdbModel model, final Iterable<? extends AnalyzedBasePair> basePairs) {
        final Map<Integer, Set<Integer>> interactions = new HashMap<>();

        for (final AnalyzedBasePair classifiedBasePair : basePairs) {
            final BasePair basePair = classifiedBasePair.basePair();
            final int left = model.indexOf(basePair.left());
            final int right = model.indexOf(basePair.right());
            interactions.computeIfAbsent(left, unused -> new HashSet<>()).add(right);
            interactions.computeIfAbsent(right, unused -> new HashSet<>()).add(left);
        }

        final Collection<AnalyzedBasePair> isolated = new ArrayList<>();

        for (final AnalyzedBasePair classifiedBasePair : basePairs) {
            final BasePair basePair = classifiedBasePair.basePair();
            final int left = model.indexOf(basePair.left());
            final int right = model.indexOf(basePair.right());

            if (BasePairAnalysis.checkIndexIsolation(left, interactions)
                    || BasePairAnalysis.checkIndexIsolation(right, interactions)) {
                isolated.add(classifiedBasePair);
            }
        }

        return isolated;
    }

    private static boolean checkIndexIsolation(
            final int index, final Map<Integer, Set<Integer>> interactions) {
        final Set<Integer> me = interactions.getOrDefault(index, Collections.emptySet());
        if (me.size() != 1) {
            return false;
        }

        final int myPair = me.iterator().next();
        final Set<Integer> prev = interactions.getOrDefault(index - 1, Collections.emptySet());
        final Set<Integer> next = interactions.getOrDefault(index + 1, Collections.emptySet());
        return BasePairAnalysis.checkIsolationForPrevious(myPair, prev)
                && BasePairAnalysis.checkIsolationForNext(myPair, next);
    }

    private static boolean checkIsolationForPrevious(final int myPair, final Iterable<Integer> prev) {
        for (final int prevPair : prev) {
            if (prevPair == (myPair + 1)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkIsolationForNext(final int myPair, final Iterable<Integer> next) {
        for (final int nextPair : next) {
            if (nextPair == (myPair - 1)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasePairAnalysis)) return false;

        BasePairAnalysis that = (BasePairAnalysis) o;

        if (!Objects.equals(represented, that.represented)) return false;
        if (!Objects.equals(canonical, that.canonical)) return false;
        if (!Objects.equals(nonCanonical, that.nonCanonical)) return false;
        if (!Objects.equals(stacking, that.stacking)) return false;
        if (!Objects.equals(basePhosphate, that.basePhosphate)) return false;
        if (!Objects.equals(baseRibose, that.baseRibose)) return false;
        if (!Objects.equals(other, that.other)) return false;
        if (!Objects.equals(interStrand, that.interStrand)) return false;
        return Objects.equals(messages, that.messages);
    }

    @Override
    public int hashCode() {
        int result = represented != null ? represented.hashCode() : 0;
        result = 31 * result + (canonical != null ? canonical.hashCode() : 0);
        result = 31 * result + (nonCanonical != null ? nonCanonical.hashCode() : 0);
        result = 31 * result + (stacking != null ? stacking.hashCode() : 0);
        result = 31 * result + (basePhosphate != null ? basePhosphate.hashCode() : 0);
        result = 31 * result + (baseRibose != null ? baseRibose.hashCode() : 0);
        result = 31 * result + (other != null ? other.hashCode() : 0);
        result = 31 * result + (interStrand != null ? interStrand.hashCode() : 0);
        result = 31 * result + (messages != null ? messages.hashCode() : 0);
        return result;
    }

    /**
     * Look for isolated base pairs selected to be represented in text format (BPSEQ/CT/dot-bracket)..
     * Delete any trace of them, so that they will not be used to propose text format
     * (BPSEQ/CT/dot-bracket) and they will not be visualized.
     *
     * @param model An instance of {@link DefaultPdbModel} required to get unique indices for
     *              residues.
     */
    public final void removeIsolatedBasePairs(final PdbModel model) {
        final Collection<AnalyzedBasePair> toDelete =
                BasePairAnalysis.findIsolatedBasePairs(model, represented);

        // delete base pairs
        for (final AnalyzedBasePair classifiedBasePair : toDelete) {
            represented.remove(classifiedBasePair);
            represented.remove(classifiedBasePair.invert());
            canonical.remove(classifiedBasePair);
            canonical.remove(classifiedBasePair.invert());
            nonCanonical.remove(classifiedBasePair);
            nonCanonical.remove(classifiedBasePair.invert());
        }
    }

    public final BasePairAnalysis filtered(final Set<PdbResidueIdentifier> residues) {
        final Predicate<AnalyzedBasePair> filter =
                cbp -> {
                    final BasePair basePair = cbp.basePair();
                    return residues.contains(PdbResidueIdentifier.from(basePair.left()))
                            || residues.contains(PdbResidueIdentifier.from(basePair.right()));
                };

        final List<AnalyzedBasePair> representedFiltered =
                represented.stream().filter(filter).collect(Collectors.toList());
        final List<AnalyzedBasePair> canonicalFiltered =
                canonical.stream().filter(filter).collect(Collectors.toList());
        final List<AnalyzedBasePair> nonCanonicalFiltered =
                nonCanonical.stream().filter(filter).collect(Collectors.toList());
        final List<AnalyzedBasePair> stackingFiltered =
                stacking.stream().filter(filter).collect(Collectors.toList());
        final List<AnalyzedBasePair> basePhosphateFiltered =
                basePhosphate.stream().filter(filter).collect(Collectors.toList());
        final List<AnalyzedBasePair> baseRiboseFiltered =
                baseRibose.stream().filter(filter).collect(Collectors.toList());
        final List<AnalyzedBasePair> otherFiltered =
                other.stream().filter(filter).collect(Collectors.toList());
        final List<AnalyzedBasePair> interStrandFiltered =
                interStrand.stream().filter(filter).collect(Collectors.toList());

        return new BasePairAnalysisBuilder()
                .withRepresented(representedFiltered)
                .withCanonical(canonicalFiltered)
                .withNonCanonical(nonCanonicalFiltered)
                .withStacking(stackingFiltered)
                .withBasePhosphate(basePhosphateFiltered)
                .withBaseRibose(baseRiboseFiltered)
                .withOther(otherFiltered)
                .withInterStrand(interStrandFiltered)
                .withMessages(new ArrayList<>(messages))
                .build();
    }

    public List<AnalyzedBasePair> getRepresented() {
        return represented;
    }

    public List<AnalyzedBasePair> getCanonical() {
        return canonical;
    }

    public List<AnalyzedBasePair> getNonCanonical() {
        return nonCanonical;
    }

    public List<AnalyzedBasePair> getStacking() {
        return stacking;
    }

    public List<AnalyzedBasePair> getBasePhosphate() {
        return basePhosphate;
    }

    public List<AnalyzedBasePair> getBaseRibose() {
        return baseRibose;
    }

    public List<AnalyzedBasePair> getOther() {
        return other;
    }

    public List<AnalyzedBasePair> getInterStrand() {
        return interStrand;
    }

    public List<String> getMessages() {
        return messages;
    }

    private BasePairAnalysis(
            List<AnalyzedBasePair> represented,
            List<AnalyzedBasePair> canonical,
            List<AnalyzedBasePair> nonCanonical,
            List<AnalyzedBasePair> stacking,
            List<AnalyzedBasePair> basePhosphate,
            List<AnalyzedBasePair> baseRibose,
            List<AnalyzedBasePair> other,
            List<AnalyzedBasePair> interStrand,
            List<String> messages) {
        this.represented = represented;
        this.canonical = canonical;
        this.nonCanonical = nonCanonical;
        this.stacking = stacking;
        this.basePhosphate = basePhosphate;
        this.baseRibose = baseRibose;
        this.other = other;
        this.interStrand = interStrand;
        this.messages = messages;
    }

    public static class BasePairAnalysisBuilder {
        private List<AnalyzedBasePair> represented;
        private List<AnalyzedBasePair> canonical;
        private List<AnalyzedBasePair> nonCanonical;
        private List<AnalyzedBasePair> stacking;
        private List<AnalyzedBasePair> basePhosphate;
        private List<AnalyzedBasePair> baseRibose;
        private List<AnalyzedBasePair> other;
        private List<AnalyzedBasePair> interStrand;
        private List<String> messages;

        public BasePairAnalysisBuilder withRepresented(List<AnalyzedBasePair> represented) {
            this.represented = represented;
            return this;
        }

        public BasePairAnalysisBuilder withCanonical(List<AnalyzedBasePair> canonical) {
            this.canonical = canonical;
            return this;
        }

        public BasePairAnalysisBuilder withNonCanonical(List<AnalyzedBasePair> nonCanonical) {
            this.nonCanonical = nonCanonical;
            return this;
        }

        public BasePairAnalysisBuilder withStacking(List<AnalyzedBasePair> stacking) {
            this.stacking = stacking;
            return this;
        }

        public BasePairAnalysisBuilder withBasePhosphate(List<AnalyzedBasePair> basePhosphate) {
            this.basePhosphate = basePhosphate;
            return this;
        }

        public BasePairAnalysisBuilder withBaseRibose(List<AnalyzedBasePair> baseRibose) {
            this.baseRibose = baseRibose;
            return this;
        }

        public BasePairAnalysisBuilder withOther(List<AnalyzedBasePair> other) {
            this.other = other;
            return this;
        }

        public BasePairAnalysisBuilder withInterStrand(List<AnalyzedBasePair> interStrand) {
            this.interStrand = interStrand;
            return this;
        }

        public BasePairAnalysisBuilder withMessages(List<String> messages) {
            this.messages = messages;
            return this;
        }

        public BasePairAnalysis build() {
            return new BasePairAnalysis(represented, canonical, nonCanonical, stacking, basePhosphate, baseRibose, other, interStrand, messages);
        }
    }
}
