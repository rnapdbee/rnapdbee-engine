package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;


import java.util.Objects;

public class ChainNumberKey {

    public final String chainIdentifier;
    public final Integer residueNumber;
    public final String icode;

    public ChainNumberKey(String chainIdentifier, Integer residueNumber, String icode) {
        this.chainIdentifier = chainIdentifier;
        this.residueNumber = residueNumber;
        this.icode = icode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChainNumberKey)) return false;
        ChainNumberKey that = (ChainNumberKey) o;
        return Objects.equals(chainIdentifier, that.chainIdentifier) &&
                Objects.equals(residueNumber, that.residueNumber) &&
                Objects.equals(icode, that.icode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chainIdentifier, residueNumber, icode);
    }
}
