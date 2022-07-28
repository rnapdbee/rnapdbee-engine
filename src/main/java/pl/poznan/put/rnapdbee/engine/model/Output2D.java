package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Output2D
 */
public class Output2D {

    @JsonProperty("bpseq")
    private List<String> bpSeq;

    @JsonProperty("ct")
    private List<String> ct;

    @JsonProperty("dotBracket")
    private String dotBracket;

    @JsonProperty("structuralElements")
    private StructuralElements structuralElements;

    @JsonProperty("outputImage")
    private OutputImage outputImage;

    public List<String> getBpseq() {
        return bpSeq;
    }

    public List<String> getCt() {
        return ct;
    }

    public String getDotBracket() {
        return dotBracket;
    }

    public StructuralElements getStructuralElements() {
        return structuralElements;
    }

    public OutputImage getOutputImage() {
        return outputImage;
    }

}
