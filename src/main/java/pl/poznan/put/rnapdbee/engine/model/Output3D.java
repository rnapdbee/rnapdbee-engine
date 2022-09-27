package pl.poznan.put.rnapdbee.engine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Output3D {

    @JsonProperty("tertiaryModels")
    List<SingleTertiaryModelOutput> tertiaryModels;

    public List<SingleTertiaryModelOutput> getTertiaryModels() {
        return tertiaryModels;
    }

    public void setTertiaryModels(List<SingleTertiaryModelOutput> tertiaryModels) {
        this.tertiaryModels = tertiaryModels;
    }
}
