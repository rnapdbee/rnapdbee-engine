package pl.poznan.put.rnapdbee.engine.shared.exception;

public class AdaptersErrorException extends RuntimeException {

    public AdaptersErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdaptersErrorException(String message) {
        super(message);
    }

}
