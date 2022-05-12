package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Output2D
 */
public class Output2D {

    @JsonProperty("bpseq")
    private BpSeq bpseq;

    @JsonProperty("ct")
    private Ct ct;

    @JsonProperty("dotBarcket")
    private DotBracket dotBarcket;

    @JsonProperty("structuralElements")
    private StructuralElements structuralElements;

    @JsonProperty("outputImage")
    private OutputImage outputImage;

    public BpSeq getBpseq() {
        return bpseq;
    }

    public Ct getCt() {
        return ct;
    }

    public DotBracket getDotBarcket() {
        return dotBarcket;
    }

    public StructuralElements getStructuralElements() {
        return structuralElements;
    }

    public OutputImage getOutputImage() {
        return outputImage;
    }

}
