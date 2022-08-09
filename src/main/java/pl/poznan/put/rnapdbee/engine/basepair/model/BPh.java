package pl.poznan.put.rnapdbee.engine.basepair.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * enum for BPh
 */
public enum BPh {

    // TODO instead of value probably @JsonProperty would do the trick.
    @JsonProperty("1")
    ONE("1"),
    @JsonProperty("2")
    TWO("2"),
    @JsonProperty("3")
    THREE("3"),
    @JsonProperty("4")
    FOUR("4"),
    @JsonProperty("5")
    FIVE("5"),
    @JsonProperty("6")
    SIX("6"),
    @JsonProperty("7")
    SEVEN("7"),
    @JsonProperty("8")
    EIGHT("8"),
    @JsonProperty("9")
    NINE("9");

    private final String value;

    BPh(String value) {
        this.value = value;
    }

}
