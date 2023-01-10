package pl.poznan.put.rnapdbee.engine.shared.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class ExceptionPattern {

    private final ZonedDateTime timestamp = ZonedDateTime.now();
    private final Integer status;
    private final String error;
    private final String message;

    /**
     * ApiException class constructor
     *
     * @param message    Exception message
     * @param httpStatus Exception status code
     */
    public ExceptionPattern(
            String message,
            HttpStatus httpStatus) {
        this.message = message;
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
