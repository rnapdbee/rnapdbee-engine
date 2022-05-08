package com.example.rnapdbeeEngine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for entry of BpSeq - fields taken from BioCommons' ImmutableEntry class
 */
public class BpSeqEntry {

    @JsonProperty("index")
    private int index;

    @JsonProperty("seq")
    private char seq;

    @JsonProperty("pair")
    private int pair;

    @JsonProperty("comment")
    private String comment;

    public int getIndex() {
        return index;
    }

    public char getSeq() {
        return seq;
    }

    public int getPair() {
        return pair;
    }

    public String getComment() {
        return comment;
    }
}