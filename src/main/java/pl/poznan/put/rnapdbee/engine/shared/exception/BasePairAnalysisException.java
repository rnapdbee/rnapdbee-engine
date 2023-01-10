package pl.poznan.put.rnapdbee.engine.shared.exception;

public class BasePairAnalysisException extends RuntimeException {

    public BasePairAnalysisException() {
        super("Base pair analysis failed.");
    }
}
