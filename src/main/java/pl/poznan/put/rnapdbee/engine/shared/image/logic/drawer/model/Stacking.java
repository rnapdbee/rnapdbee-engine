package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Stacking {
  @JsonProperty("id1")
  public int id1;

  @JsonProperty("id2")
  public int id2;

  @JsonProperty("color")
  public String color;

  @JsonProperty("thickness")
  public Double thickness;

  @Override
  public String toString() {
    return "Stacking{"
        + "id1="
        + id1
        + ", id2="
        + id2
        + ", color='"
        + color
        + '\''
        + ", thickness="
        + thickness
        + '}';
  }
}
