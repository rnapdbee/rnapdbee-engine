package pl.poznan.put.rnapdbee.engine.calculation.tertiary.domain;


import pl.poznan.put.pdb.PdbNamedResidueIdentifier;

/**
 * Class representing named residue returned to the REST consumer.
 */
public class OutputNamedResidue {

    private String chainIdentifier;

    private int residueNumber;

    private String insertionCode;

    private char oneLetterName;

    public static OutputNamedResidue fromPdbNamedResidueIdentifier(PdbNamedResidueIdentifier pdbNamedResidueIdentifier) {
        OutputNamedResidue outputNamedResidue = new OutputNamedResidue();
        outputNamedResidue.setResidueNumber(pdbNamedResidueIdentifier.residueNumber());
        outputNamedResidue.setChainIdentifier(pdbNamedResidueIdentifier.chainIdentifier());
        outputNamedResidue.setOneLetterName(pdbNamedResidueIdentifier.oneLetterName());
        outputNamedResidue.setInsertionCode(
                pdbNamedResidueIdentifier.insertionCode()
                        .orElse(null));
        return outputNamedResidue;
    }

    public String getChainIdentifier() {
        return chainIdentifier;
    }

    public void setChainIdentifier(String chainIdentifier) {
        this.chainIdentifier = chainIdentifier;
    }

    public int getResidueNumber() {
        return residueNumber;
    }

    public void setResidueNumber(int residueNumber) {
        this.residueNumber = residueNumber;
    }

    public String getInsertionCode() {
        return insertionCode;
    }

    public void setInsertionCode(String insertionCode) {
        this.insertionCode = insertionCode;
    }

    public char getOneLetterName() {
        return oneLetterName;
    }

    public void setOneLetterName(char oneLetterName) {
        this.oneLetterName = oneLetterName;
    }
}
