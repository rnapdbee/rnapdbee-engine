package com.example.rnapdbeeEngine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for entry of Ct - fields taken from the BioCommons' ImmutableExtendedEntry class
 */
public class CtExtendedEntry {

    @JsonProperty("index")
    private int index;

    @JsonProperty("seq")
    private char seq;

    @JsonProperty("before")
    private int before;

    @JsonProperty("after")
    private int after;

    @JsonProperty("pair")
    private int pair;

    @JsonProperty("original")
    private int original;

    @JsonProperty("comment")
    private String comment;

    public int getIndex() {
        return index;
    }

    public char getSeq() {
        return seq;
    }

    public int getBefore() {
        return before;
    }

    public int getAfter() {
        return after;
    }

    public int getPair() {
        return pair;
    }

    public int getOriginal() {
        return original;
    }

    public String getComment() {
        return comment;
    }
}
