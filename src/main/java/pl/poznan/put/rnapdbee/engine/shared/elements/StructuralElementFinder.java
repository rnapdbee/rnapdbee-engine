package pl.poznan.put.rnapdbee.engine.shared.elements;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.pdb.analysis.ResidueCollection;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputType;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.ImmutableStrandView;
import pl.poznan.put.structure.formats.Strand;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StructuralElementFinder {
    // ([^-]*)(.*?-+)*([^-]*)
    // Examples:
    // )}...------------------------...[[[[[[...-(
    // )....-------------
    // ----------......(
    // ).....-------------.....(
    // )...---...--..---...---....(
    private static final Logger LOGGER = LoggerFactory.getLogger(StructuralElementFinder.class);
    private static final Pattern WITH_MISSING = Pattern.compile("([^-]*)(.*?-+)*([^-]*)");
    private final DotBracket dotBracket;
    private final boolean canElementsEndWithPseudoknots;
    private final boolean reuseSingleStrandsFromLoops;
    private final List<StructuralElement> singleStrands5p;
    private final List<StructuralElement> singleStrands3p;
    private final List<StructuralElement> singleStrands;
    private final List<StructuralElement> stems;
    private final List<StructuralElement> loops;

    public StructuralElementFinder(
            final DotBracket dotBracket,
            final boolean canElementsEndWithPseudoknots,
            final boolean reuseSingleStrandsFromLoops) {
        super();
        this.dotBracket = dotBracket;
        this.canElementsEndWithPseudoknots = canElementsEndWithPseudoknots;
        this.reuseSingleStrandsFromLoops = reuseSingleStrandsFromLoops;

        if (StringUtils.containsAny(dotBracket.structure(), "(")) {
            final BpSeq bpSeq = BpSeq.fromDotBracket(dotBracket);
            final List<BpSeq.Entry> entries = new ArrayList<>(bpSeq.entries());

            stems = findStructuralElements(entries, this::findStems);
            loops = findStructuralElements(entries, this::findLoops);
            singleStrands5p = findStructuralElements(entries, this::findSingleStrand5p);
            singleStrands3p = findStructuralElements(entries, this::findSingleStrand3p);
            singleStrands = findStructuralElements(entries, this::findSingleStrands);

            splitSingleStrandsWithMissingSymbols();
        } else {
            stems = Collections.emptyList();
            loops = Collections.emptyList();
            singleStrands5p = Collections.emptyList();
            singleStrands3p = Collections.emptyList();
            singleStrands = Collections.emptyList();
        }
    }

    public String generateCoordinates(final ResidueCollection wholeStructure, final InputType inputType) {
        if (noStructuralElementsFound()) {
            return "";
        }
        if (inputType == InputType.PDB) {
            ResidueCollection.PdbBuilder pdbBuilder = new ResidueCollection.PdbBuilder();

            addToPdbBuilder(pdbBuilder, wholeStructure, "D", stems, true);
            addToPdbBuilder(pdbBuilder, wholeStructure, "L", loops, true);
            addToPdbBuilder(pdbBuilder, wholeStructure, "SS", singleStrands, true);
            addToPdbBuilder(pdbBuilder, wholeStructure, "SS5p", singleStrands5p, false);
            addToPdbBuilder(pdbBuilder, wholeStructure, "SS3p", singleStrands3p, false);

            return pdbBuilder.build();
        } else if (inputType == InputType.MMCIF) {
            ResidueCollection.CifBuilder cifBuilder = new ResidueCollection.CifBuilder();

            addToCifBuilder(cifBuilder, wholeStructure, "D", stems, true);
            addToCifBuilder(cifBuilder, wholeStructure, "L", loops, true);
            addToCifBuilder(cifBuilder, wholeStructure, "SS", singleStrands, true);
            addToCifBuilder(cifBuilder, wholeStructure, "SS5p", singleStrands5p, false);
            addToCifBuilder(cifBuilder, wholeStructure, "SS3p", singleStrands3p, false);

            try {
                return cifBuilder.build();
            } catch (IOException e) {
                LOGGER.error("generation of CIF coordinates threw IOException.", e);
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Method generateCoordinates supports only PDB and MMCIF InputTypes.");
        }
    }

    private boolean noStructuralElementsFound() {
        return singleStrands5p.size() == 0 &&
                singleStrands3p.size() == 0 &&
                singleStrands.size() == 0 &&
                stems.size() == 0 &&
                loops.size() == 0;
    }

    private void addToPdbBuilder(ResidueCollection.PdbBuilder pdbBuilder,
                                 ResidueCollection wholeStructure,
                                 String abbreviation,
                                 List<StructuralElement> structuralElements,
                                 boolean addIndex) {
        for (int i = 0; i < structuralElements.size(); i++) {
            StructuralElement element = structuralElements.get(i);
            StructuralElementFromPdb structuralElement = (StructuralElementFromPdb) element;
            ResidueCollection residues = structuralElement.apply(wholeStructure);
            String name = addIndex
                    ? abbreviation + (i + 1)
                    : abbreviation;
            pdbBuilder.add(residues, name);
        }
    }

    private void addToCifBuilder(ResidueCollection.CifBuilder cifBuilder,
                                 ResidueCollection wholeStructure,
                                 String abbreviation,
                                 List<StructuralElement> structuralElements,
                                 boolean addIndex) {
        for (int i = 0; i < structuralElements.size(); i++) {
            StructuralElement element = structuralElements.get(i);
            StructuralElementFromPdb structuralElement = (StructuralElementFromPdb) element;
            ResidueCollection residues = structuralElement.apply(wholeStructure);
            String name = addIndex
                    ? abbreviation + (i + 1)
                    : abbreviation;
            cifBuilder.add(residues, name, element.toString());
        }
    }

    private static void addStructuralElement(
            final List<StructuralElement> elements, final StructuralElement newElement) {
        if (!elements.contains(newElement)) {
            int first = 0;
            int last = CollectionUtils.size(elements);
            while (first != last) {
                final int middle = (first + last) / 2;
                if (elements.get(middle).compareTo(newElement) <= 0) {
                    first = middle + 1;
                } else {
                    last = middle;
                }
            }
            if (first < CollectionUtils.size(elements)) {
                elements.add(first, newElement);
            } else {
                elements.add(newElement);
            }
        }
    }

    private static void updateBuilder(
            final StringBuilder builder,
            final String message,
            final String abbreviation,
            final List<? extends StructuralElement> elements,
            final boolean addIndex) {
        if (!elements.isEmpty()) {
            for (int i = 0; i < elements.size(); i++) {
                final StructuralElement element = elements.get(i);
                builder.append(message);
                builder.append(' ');
                builder.append(abbreviation);
                if (addIndex) {
                    builder.append(i + 1);
                }
                builder.append(' ');
                builder.append(element);
                builder.append('\n');
            }
            builder.append('\n');
        }
    }

    public final DotBracket getDotBracket() {
        return dotBracket;
    }

    public final List<StructuralElement> getSingleStrands5p() {
        return Collections.unmodifiableList(singleStrands5p);
    }

    public final List<StructuralElement> getSingleStrands3p() {
        return Collections.unmodifiableList(singleStrands3p);
    }

    public final List<StructuralElement> getSingleStrands() {
        return Collections.unmodifiableList(singleStrands);
    }

    public final List<StructuralElement> getStems() {
        return Collections.unmodifiableList(stems);
    }

    public final List<StructuralElement> getLoops() {
        return Collections.unmodifiableList(loops);
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        StructuralElementFinder.updateBuilder(builder, "Stem", "D", stems, true);
        StructuralElementFinder.updateBuilder(builder, "Loop", "L", loops, true);
        StructuralElementFinder.updateBuilder(builder, "Single strand", "SS", singleStrands, true);
        StructuralElementFinder.updateBuilder(
                builder, "Single strand 5'", "SS5p", singleStrands5p, false);
        StructuralElementFinder.updateBuilder(
                builder, "Single strand 3'", "SS3p", singleStrands3p, false);
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    private List<StructuralElement> findStructuralElements(
            final List<BpSeq.Entry> entries, final SpecificFinder finder) {
        final Set<StructuralElement> set = new TreeSet<>();
        if (canElementsEndWithPseudoknots) {
            set.addAll(finder.find(entries, false));
        }
        set.addAll(finder.find(entries, true));
        return new ArrayList<>(set);
    }

    private void splitSingleStrandsWithMissingSymbols() {
        splitSingleStrands(loops);
        splitSingleStrands(singleStrands);
        splitSingleStrands(singleStrands5p);
        splitSingleStrands(singleStrands3p);

        // sanity check
        final Stream<StructuralElement> elementStream =
                Stream.of(singleStrands, singleStrands5p, singleStrands3p).flatMap(Collection::stream);
        final List<StructuralElement> strandsWithMissing =
                elementStream
                        .filter(
                                element -> StringUtils.indexOf(element.getStrands().get(0).structure(), '-') >= 0)
                        .collect(Collectors.toList());
        assert strandsWithMissing.isEmpty() : Arrays.toString(strandsWithMissing.toArray());
    }

    private void splitSingleStrands(final List<? extends StructuralElement> elements) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            final StructuralElement element = elements.get(i);
            final List<Strand> strands = element.getStrands();
            // if (strands.size() == 1) {
            //    final Strand strand = strands.get(0);
            boolean atLeastOneStrandIncludesMissingResidues = false;
            for (final Strand strand : strands) {
                if (strand.containsMissing()) {
                    atLeastOneStrandIncludesMissingResidues = true;
                    final String structure = strand.structure();
                    final Matcher matcher = StructuralElementFinder.WITH_MISSING.matcher(structure);

                    if (matcher.matches()) {
                        // handle possible 3' strand
                        final String fragment3p = Optional.ofNullable(matcher.group(1)).orElse("");
                        final int length3p = fragment3p.length();

                        if ((length3p > 1) && DotBracketSymbol.isPairing(fragment3p.charAt(0))) {
                            final int from = strand.begin();
                            final Strand strand3p =
                                    ImmutableStrandView.of("single strand 3'", dotBracket, from, from + length3p);
                            final StructuralElement ss3p = StructuralElement.createInstance(dotBracket, strand3p);
                            StructuralElementFinder.addStructuralElement(singleStrands3p, ss3p);
                        }

                        // handle possible 5' strand
                        final String fragment5p = Optional.ofNullable(matcher.group(3)).orElse("");
                        final int length5p = fragment5p.length();

                        if ((length5p > 1) && DotBracketSymbol.isPairing(fragment5p.charAt(length5p - 1))) {
                            final int to = strand.end();
                            final Strand strand5p =
                                    ImmutableStrandView.of("single strand 5'", dotBracket, to - length5p, to);
                            final StructuralElement ss5p = StructuralElement.createInstance(dotBracket, strand5p);
                            StructuralElementFinder.addStructuralElement(singleStrands5p, ss5p);
                        }
                    }
                }
            }
            if (atLeastOneStrandIncludesMissingResidues) {
                elements.remove(i);
            }
        }
    }

    private Collection<StructuralElement> findSingleStrand5p(
            final List<? extends BpSeq.Entry> entries, final boolean treatPseudoknotAsUnpaired) {
        final int size = entries.size();
        int to = 0;

        // seek forward until first pair is found
        for (; to < size; to++) {
            final BpSeq.Entry entry = entries.get(to);
            if (entry.isPaired()) {
                if (treatPseudoknotAsUnpaired && (dotBracket.symbols().get(to).order() > 0)) {
                    continue;
                }

                break;
            }
        }

        // ignore missing nucleotides from the front
        int from = 0;
        for (; from < to; from++) {
            if (!dotBracket.symbols().get(from).isMissing()) {
                break;
            }
        }

        if (to > 0) {
            final Strand strand = ImmutableStrandView.of("single strand 5'", dotBracket, from, to + 1);
            return Collections.singleton(StructuralElement.createInstance(dotBracket, strand));
        }

        return Collections.emptyList();
    }

    private Collection<StructuralElement> findSingleStrand3p(
            final List<? extends BpSeq.Entry> entries, final boolean treatPseudoknotAsUnpaired) {
        final int size = entries.size();
        int from = size - 1;

        // seek backwards until first pair is found
        for (; from >= 0; from--) {
            final BpSeq.Entry entry = entries.get(from);
            if (entry.isPaired()) {
                if (treatPseudoknotAsUnpaired && (dotBracket.symbols().get(from).order() > 0)) {
                    continue;
                }
                break;
            }
        }

        // ignore missing nucleotides in the back
        int to = size;
        for (; to > from; to--) {
            if (!dotBracket.symbols().get(to - 1).isMissing()) {
                break;
            }
        }

        if ((from + 1) < size) {
            final Strand strand = ImmutableStrandView.of("single strand 3'", dotBracket, from, to);
            return Collections.singleton(StructuralElement.createInstance(dotBracket, strand));
        }

        return Collections.emptyList();
    }

    private Collection<StructuralElement> findSingleStrands(
            final List<? extends BpSeq.Entry> entries, final boolean treatPseudoknotAsUnpaired) {
        final Collection<StructuralElement> elements = new ArrayList<>();
        final int size = entries.size();
        int from = -1;

        for (int i = 0; i < size; i++) {
            final BpSeq.Entry entry = entries.get(i);

            if (entry.isPaired()) {
                if (treatPseudoknotAsUnpaired && (dotBracket.symbols().get(i).order() > 0)) {
                    continue;
                }

                final int j = entry.pair() - 1;

                if (from != -1) { // not first pair
                    if (from != (i - 1)) { // not part of stem
                        if (from != j) { // not hairpin
                            final Strand strand =
                                    ImmutableStrandView.of("single strand", dotBracket, from, i + 1);

                            if (reuseSingleStrandsFromLoops || isStrandIndependent(strand)) {
                                final StructuralElement singleStrand =
                                        StructuralElement.createInstance(dotBracket, strand);
                                elements.add(singleStrand);
                            }
                        }
                    }
                }

                from = i;
            }
        }

        return elements;
    }

    // returns true if strand is not part of any loop
    private boolean isStrandIndependent(final Strand strand) {
        return loops.stream()
                .flatMap(loop -> loop.getStrands().stream())
                .noneMatch(loopStrand -> loopStrand.equals(strand));
    }

    private Collection<StructuralElement> findStems(
            final List<? extends BpSeq.Entry> entries, final boolean treatPseudoknotAsUnpaired) {
        final Collection<StructuralElement> elements = new ArrayList<>();
        final int size = entries.size();
        int fromFirst = -1;
        int fromSecond = -1;
        int toSecond = -1;

        for (int i = 0; i < size; i++) {
            final BpSeq.Entry entry = entries.get(i);

            if (entry.isPaired()) {
                final int j = entry.pair() - 1;

                if (treatPseudoknotAsUnpaired && (dotBracket.symbols().get(i).order() > 0)) {
                    if (fromFirst != -1) {
                        // stem finished by pseudoknot which is seen as unpaired
                        addStem(elements, fromFirst, i, fromSecond, toSecond);
                        fromFirst = i;
                        toSecond = j + 1;
                        fromSecond = j;
                    }
                    continue;
                }

                if (i < j) { // avoid processing the same stem two times
                    if (fromFirst == -1) { // first pair of a stem
                        fromFirst = i;
                        toSecond = j + 1;
                        fromSecond = j;
                    } else if (j == (fromSecond - 1)) { // continuation of stem
                        fromSecond = j;
                    } else {
                        // stem finished by paired nucleotide forming new stem
                        addStem(elements, fromFirst, i, fromSecond, toSecond);
                        fromFirst = i;
                        toSecond = j + 1;
                        fromSecond = j;
                    }
                    continue;
                }
            }

            if (fromFirst != -1) { // stem finished by unpaired nucleotide
                addStem(elements, fromFirst, i, fromSecond, toSecond);
            }
            fromFirst = -1;
            fromSecond = -1;
            toSecond = -1;
        }

        // sanity check
        for (final StructuralElement element : elements) {
            final List<Strand> strands = element.getStrands();
            assert strands.size() == 2 : "Stems must have exactly 2 strands";

            final Strand first = strands.get(0);
            final Strand second = strands.get(1);
            assert first.length() == second.length()
                    : "Stems must have strands of exactly the same length:\n" + element;
        }

        return elements;
    }

    private void addStem(
            final Collection<? super StructuralElement> blocks,
            final int fromFirst,
            final int toFirst,
            final int fromSecond,
            final int toSecond) {
        // check if both strands are longer than 1
        if (((toSecond - fromSecond) > 1) && ((fromSecond - fromFirst) > 1)) {
            final Strand first = ImmutableStrandView.of("stem first", dotBracket, fromFirst, toFirst);
            final Strand second = ImmutableStrandView.of("stem second", dotBracket, fromSecond, toSecond);
            final StructuralElement stem = StructuralElement.createInstance(dotBracket, first, second);
            blocks.add(stem);
        }
    }

    private Collection<StructuralElement> findLoops(
            final List<? extends BpSeq.Entry> entries, final boolean treatPseudoknotAsUnpaired) {
        final Deque<Strand> candidates = new ArrayDeque<>();
        final int size = entries.size();
        int from = -1;

        for (int i = 0; i < size; i++) {
            final BpSeq.Entry entry = entries.get(i);

            if (entry.isPaired()) {
                final int j = entry.pair() - 1;

                if (treatPseudoknotAsUnpaired && (dotBracket.symbols().get(i).order() > 0)) {
                    continue;
                }

                // all fragments between two pairs, even of zero-length such
                // as "((", are candidates for a loop
                if (from != -1) {
                    final Strand strand = ImmutableStrandView.of("loop", dotBracket, from, i + 1);
                    candidates.add(strand);
                }
                from = i;
            }
        }

        final Collection<StructuralElement> elements = new ArrayList<>();
        final List<Strand> blockStrands = new ArrayList<>();

        /*
         * The algorithm is following:
         * 1. Get the first candidate
         * 2. See where its end point to
         * 3. Look for strand which starts with that
         * 4. Repeat until one of the following happens
         *    a) Cannot find the next strand -> happens when the current strand
         *       is a 5' or 3' single strand having only one paired end
         *    b) The next strand is in fact the one we started with in point
         *       1. -> this means we found a real loop!
         */
        while (!candidates.isEmpty()) {
            Strand current = candidates.getFirst();
            final Strand starting = current;
            boolean found;

            do {
                blockStrands.add(current);
                final int to = current.end();
                final int nextFrom = entries.get(to - 1).pair() - 1;
                found = false;

                for (final Strand next : candidates) {
                    if (next.begin() == nextFrom) {
                        current = next;
                        found = true;
                        break;
                    }
                }
            } while (found && !current.equals(starting));

            boolean isValid = true;
            if (blockStrands.size() == 1) {
                final Strand strand = blockStrands.get(0);
                final int strandFrom = strand.begin();
                final int strandTo = strand.end();
                if (entries.get(strandFrom).pair() != strandTo) {
                    isValid = false;
                }
            }

            final boolean isBasePairStep = blockStrands.size() == 2
                    && blockStrands.stream().allMatch(strand -> strand.length() <= 2);

            if (found && isValid && !isBasePairStep) {
                final StructuralElement loop = StructuralElement.createInstance(dotBracket, blockStrands);
                elements.add(loop);
            }

            for (final Strand strand : blockStrands) {
                candidates.removeAll(blockStrands);
            }

            blockStrands.clear();
        }

        // sanity check
        for (final StructuralElement element : elements) {
            final List<Strand> elementStrands = element.getStrands();

            for (int i = 1, strandCount = elementStrands.size(); i < strandCount; i++) {
                final Strand previous = elementStrands.get(i - 1);
                final Strand current = elementStrands.get(i);

                final int closing = current.begin();
                final int opening = previous.end() - 1;
                final int pair = entries.get(opening).pair() - 1;

                assert pair == closing : element;
                assert dotBracket.symbols().get(closing).order()
                        == dotBracket.symbols().get(opening).order();
            }

            final Strand first = elementStrands.get(0);
            final Strand last = elementStrands.get(elementStrands.size() - 1);

            final int closing = first.begin();
            final int opening = last.end() - 1;
            final int pair = entries.get(opening).pair() - 1;

            assert pair == closing : element;
            assert dotBracket.symbols().get(closing).order()
                    == dotBracket.symbols().get(opening).order();
        }

        return elements;
    }

    @FunctionalInterface
    private interface SpecificFinder {
        Collection<StructuralElement> find(
                List<BpSeq.Entry> entries, boolean treatPseudoknotAsUnpaired);
    }
}
