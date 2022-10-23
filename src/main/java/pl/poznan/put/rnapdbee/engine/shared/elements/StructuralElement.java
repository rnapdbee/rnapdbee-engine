package pl.poznan.put.rnapdbee.engine.shared.elements;

import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.DotBracketFromPdb;
import pl.poznan.put.structure.formats.Strand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StructuralElement implements Comparable<StructuralElement> {
    protected final DotBracket dotBracket;
    protected final List<Strand> strands;

    protected StructuralElement(final DotBracket dotBracket, final List<Strand> strands) {
        super();
        this.dotBracket = dotBracket;
        this.strands = new ArrayList<>(strands);
    }

    protected StructuralElement(final DotBracket dotBracket, final Strand... strands) {
        super();
        this.dotBracket = dotBracket;
        this.strands = Arrays.asList(strands);
    }

    public static StructuralElement createInstance(
            final DotBracket dotBracket, final List<Strand> strands) {
        if (dotBracket instanceof DotBracketFromPdb) {
            return new StructuralElementFromPdb((DotBracketFromPdb) dotBracket, strands);
        }
        return new StructuralElement(dotBracket, strands);
    }

    public static StructuralElement createInstance(
            final DotBracket dotBracket, final Strand... strands) {
        if (dotBracket instanceof DotBracketFromPdb) {
            return new StructuralElementFromPdb((DotBracketFromPdb) dotBracket, strands);
        }
        return new StructuralElement(dotBracket, strands);
    }

    public final List<Strand> getStrands() {
        return Collections.unmodifiableList(strands);
    }

    @Override
    public final String toString() {

        return IntStream.range(1, strands.size())
                .mapToObj(strands::get)
                .map(strand -> ' ' + strand.description())
                .collect(Collectors.joining("", strands.get(0).description(), ""));
    }

    @Override
    public final int compareTo(final StructuralElement t) {
        for (int i = 0; i < Math.min(strands.size(), t.strands.size()); i++) {
            final Strand mine = strands.get(i);
            final Strand their = t.strands.get(i);
            if (mine.begin() != their.begin()) {
                return Integer.compare(mine.begin(), their.begin());
            }
            if (mine.end() != their.end()) {
                return Integer.compare(mine.end(), their.end());
            }
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StructuralElement that = (StructuralElement) o;

        return strands.equals(that.strands);
    }

    @Override
    public int hashCode() {
        return strands.hashCode();
    }
}
