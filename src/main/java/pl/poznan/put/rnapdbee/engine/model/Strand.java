package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO class for Strand - fields structure taken from BioCommons' ImmutableDefaultStrand class
 */
public class Strand {

    @JsonProperty("combineStrands")
    private List<DotBracket> combineStrands;

    @JsonProperty("name")
    private String name;

    @JsonProperty("symbols")
    private List<DotBracketSymbol> symbols;

    public List<DotBracket> getCombineStrands() {
        return combineStrands;
    }

    public String getName() {
        return name;
    }

    public List<DotBracketSymbol> getSymbols() {
        return symbols;
    }
}
