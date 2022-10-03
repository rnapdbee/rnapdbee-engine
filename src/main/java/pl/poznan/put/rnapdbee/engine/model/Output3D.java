package pl.poznan.put.rnapdbee.engine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Output3D {

    @JsonProperty("models")
    List<SingleTertiaryModelOutput> models;

    public List<SingleTertiaryModelOutput> getModels() {
        return models;
    }

    public void setModels(List<SingleTertiaryModelOutput> models) {
        this.models = models;
    }
}
