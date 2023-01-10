package pl.poznan.put.rnapdbee.engine.shared.exception;

/**
 * Exception thrown whenever filename can not be resolved from Content-Disposition header
 */
public class ImproperContentDispositionException extends RuntimeException {

    public ImproperContentDispositionException() {
        super("Invalid Content-Disposition header");
    }

}
