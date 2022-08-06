package pl.poznan.put.rnapdbee.engine.basepair.model;


/**
 * enum for BPh
 */
public enum BPh {

    // TODO instead of value probably @JsonProperty would do the trick.
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9");

    private final String value;

    BPh(String value) {
        this.value = value;
    }

}
