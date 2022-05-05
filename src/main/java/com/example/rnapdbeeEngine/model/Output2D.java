package com.example.rnapdbeeEngine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Output2D
 */
public class Output2D {
    @JsonProperty("bpseq")
    private BPSEQ bpseq;

    @JsonProperty("ct")
    private CT ct;

    @JsonProperty("dotBarcket")
    private DotBracket dotBarcket;

    @JsonProperty("structuralElements")
    private StructuralElements structuralElements;

    @JsonProperty("outputImage")
    private OutputImage outputImage;

    public BPSEQ getBpseq() {
        return bpseq;
    }

    public CT getCt() {
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
