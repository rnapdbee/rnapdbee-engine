package com.example.rnapdbeeEngine.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO class for Ct - fields structure taken from the BioCommons' ImmutableCt class
 */
public class Ct {

    @JsonProperty("entries")
    /* In the BioCommons this is SortedSet, but we could stick to the list as for the DTO. */
    private List<CtExtendedEntry> entries;

    public List<CtExtendedEntry> getEntries() {
        return entries;
    }
}
