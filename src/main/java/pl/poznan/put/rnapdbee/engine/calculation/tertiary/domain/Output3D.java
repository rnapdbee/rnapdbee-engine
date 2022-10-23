package pl.poznan.put.rnapdbee.engine.calculation.tertiary.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Output3D {

    @JsonProperty("models")
    List<SingleTertiaryModelOutput> models;

    @JsonProperty("title")
    private String title;

    public List<SingleTertiaryModelOutput> getModels() {
        return models;
    }

    public void setModels(List<SingleTertiaryModelOutput> models) {
        this.models = models;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
