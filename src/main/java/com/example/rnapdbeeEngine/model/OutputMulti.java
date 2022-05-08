package com.example.rnapdbeeEngine.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;


/**
 * DTO class for OutputMulti
 */
public class OutputMulti {

    @JsonProperty("entries")
    private List<OutputMultiEntry> entries = new ArrayList<>();

    public List<OutputMultiEntry> getEntries() {
        return entries;
    }

}
