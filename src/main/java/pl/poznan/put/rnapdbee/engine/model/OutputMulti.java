package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


/**
 * DTO class for OutputMulti
 */
public class OutputMulti {

    @JsonProperty("entries")
    private List<OutputMultiEntry> entries;

    public List<OutputMultiEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<OutputMultiEntry> entries) {
        this.entries = entries;
    }
}
