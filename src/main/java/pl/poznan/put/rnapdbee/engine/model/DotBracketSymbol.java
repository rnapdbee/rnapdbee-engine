package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for Dot Bracket Symbol - fields structure taken from BioCommons' ImmutableDotBracketSymbol class
 */
public class DotBracketSymbol {

    @JsonProperty("sequence")
    private char sequence;

    @JsonProperty("structure")
    private char structure;

    @JsonProperty("index")
    private int index;

    public char getSequence() {
        return sequence;
    }

    public char getStructure() {
        return structure;
    }

    public int getIndex() {
        return index;
    }
}
