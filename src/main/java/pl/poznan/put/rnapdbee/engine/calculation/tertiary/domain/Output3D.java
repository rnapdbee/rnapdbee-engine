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

    public String getTitle() {
        return title;
    }

    private Output3D(List<SingleTertiaryModelOutput> models, String title) {
        this.models = models;
        this.title = title;
    }

    public static class Output3DBuilder {
        private List<SingleTertiaryModelOutput> models;
        private String title;

        public Output3DBuilder withModels(List<SingleTertiaryModelOutput> models) {
            this.models = models;
            return this;
        }

        public Output3DBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Output3D build() {
            return new Output3D(models, title);
        }
    }
}
