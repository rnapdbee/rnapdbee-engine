package pl.poznan.put.rnapdbee.engine.shared.exception;

public class ConverterException extends RuntimeException {

    public ConverterException() {
        super("Failed to convert input into dotBracket.");
    }
}
