package pl.poznan.put.rnapdbee.engine.calculation.model;

public enum SecondaryFileExtensionEnum {

    BP_SEQ("bpseq"),
    CT("ct"),
    DBN("dbn");

    public final String fileExtension;

    SecondaryFileExtensionEnum(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}
