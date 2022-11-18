package pl.poznan.put.rnapdbee.engine.calculation.testhelp.shared.converter;

public class Range implements Comparable<Range> {
    public final int left;
    public final int right;

    public Range(final int l, final int r) {
        super();
        left = l;
        right = r;
    }

    public final int size() {
        return Math.abs(left - right);
    }

    public final boolean contains(final int node) {
        return ((node <= right) && (node >= left));
    }

    @Override
    public final int compareTo(final Range t) {
        if (size() < t.size()) return -1;
        if (size() > t.size()) return 1;
        if (left < t.left) return -1;
        return 1;
    }
}
