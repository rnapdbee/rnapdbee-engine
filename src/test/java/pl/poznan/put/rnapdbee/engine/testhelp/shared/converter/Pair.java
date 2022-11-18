package pl.poznan.put.rnapdbee.engine.testhelp.shared.converter;

public class Pair {
    private final int ST;
    private final int ND;

    public Pair(final int _ST, final int _ND) {
        super();
        ST = _ST;
        ND = _ND;
    }

    public Pair(final Pair rhs) {
        super();
        ST = rhs.ST;
        ND = rhs.ND;
    }

    public final int getFirst() {
        return ST;
    }

    public final int getSecond() {
        return ND;
    }

    @Override
    public int hashCode() {
        return ST * 10_000 + ND;
    }

    @Override
    public boolean equals(Object obj) {
        if (this.hashCode() != obj.hashCode()) {
            return false;
        }

        if (!(obj instanceof Pair)) {
            return false;
        }

        Pair anotherPair = (Pair) obj;
        return anotherPair.getFirst() == this.getFirst() &&
                anotherPair.getSecond() == this.getSecond();
    }
}
