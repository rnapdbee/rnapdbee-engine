package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * DTO class for Dot Bracket - fields structure taken from BioCommons' ImmutableDefaultDotBracket class
 */
public class DotBracket {

    @JsonProperty("strands")
    private List<Strand> strands;

    @JsonProperty("sequence")
    private String sequence;

    @JsonProperty("structure")
    private String structure;

    @JsonProperty("lazyInitBitmap")
    private long lazyInitBitmap;

    @JsonProperty("pairs")
    /* We're probably going to need a custom mapper if we'd want to map properties into a map using Spring mechanisms,
     * leaving Map for now */
    private Map<DotBracketSymbol, DotBracketSymbol> pairs;

}
