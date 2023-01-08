package pl.poznan.put.rnapdbee.engine.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {
            ImproperContentDispositionException.class,
            ImproperStructureFormatException.class,
            UnknownFileExtensionException.class,
            NoRnaModelsInFileException.class,
    })
    public ResponseEntity<ExceptionPattern> handleBadRequestException(RuntimeException exception) {
        ExceptionPattern exceptionPattern = new ExceptionPattern(exception.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(exceptionPattern, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            BasePairAnalysisException.class,
            ConsensualVisualizationException.class,
            ConverterException.class,
            CifParsingException.class
    })
    public ResponseEntity<ExceptionPattern> handleInternalServerErrorException(RuntimeException exception) {
        ExceptionPattern exceptionPattern = new ExceptionPattern(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(exceptionPattern, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
