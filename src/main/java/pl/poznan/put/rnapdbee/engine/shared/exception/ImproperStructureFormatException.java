package pl.poznan.put.rnapdbee.engine.shared.exception;

public class ImproperStructureFormatException extends RuntimeException {

    public ImproperStructureFormatException(String message) {
        super(message);
    }

    public ImproperStructureFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}
