package pl.poznan.put.rnapdbee.engine.shared.exception;

public class CifParsingException extends RuntimeException {

    public CifParsingException() {
        super("Parsing of CIF file failed.");
    }
}
