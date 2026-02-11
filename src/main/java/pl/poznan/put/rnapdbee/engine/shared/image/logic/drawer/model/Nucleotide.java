package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Nucleotide {
  @JsonProperty("id")
  public int id;

  @JsonProperty("number")
  public int number;

  @JsonProperty("char")
  public String character;

  @JsonProperty("outlineColor")
  public String outlineColor;

  @JsonProperty("innerColor")
  public String innerColor;

  @JsonProperty("nameColor")
  public String nameColor;

  @Override
  public String toString() {
    return "Nucleotide{"
        + "id="
        + id
        + ", number="
        + number
        + ", character='"
        + character
        + '\''
        + ", outlineColor='"
        + outlineColor
        + '\''
        + ", innerColor='"
        + innerColor
        + '\''
        + ", nameColor='"
        + nameColor
        + '\''
        + '}';
  }
}
