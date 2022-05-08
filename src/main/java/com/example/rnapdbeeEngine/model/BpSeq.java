package com.example.rnapdbeeEngine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO class for BpSeq - fields from BioCommons' ImmutableBpSeq class
 */
public class BpSeq {

    @JsonProperty("entries")
    /* In the BioCommons this is SortedSet, but we could stick to the list as for the DTO. */
    private List<BpSeqEntry> entries;

    public List<BpSeqEntry> getEntries() {
        return entries;
    }
}
