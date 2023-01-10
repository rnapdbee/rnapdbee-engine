package pl.poznan.put.rnapdbee.engine.shared.exception;

public class NoRnaModelsInFileException extends RuntimeException {

    public NoRnaModelsInFileException() {
        super("No RNA models found in given file.");
    }

}
