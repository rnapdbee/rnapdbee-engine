package pl.poznan.put.rnapdbee.engine.shared.domain;

import org.apache.commons.lang3.StringUtils;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DefaultDotBracket;

/**
 *
 */
public enum InputType {
    PDB(StructureType.STRUCTURE_3D, ".pdb"),
    MMCIF(StructureType.STRUCTURE_3D, ".cif"),
    BPSEQ(StructureType.STRUCTURE_2D, ".bpseq"),
    CT(StructureType.STRUCTURE_2D, ".ct"),
    DOT_BRACKET(StructureType.STRUCTURE_2D, ".dbn");

    private final StructureType structureType;
    private final String fileExtension;

    InputType(final StructureType structureType, final String fileExtension) {
        this.structureType = structureType;
        this.fileExtension = fileExtension;
    }

    public static InputType detect(final String content) throws IllegalArgumentException {
        for (final String line : StringUtils.split(content, '\n')) {
            if (line.startsWith("_atom_site")) {
                return InputType.MMCIF;
            }
            if (line.startsWith("ATOM") || line.startsWith("HETATM")) {
                return InputType.PDB;
            }
        }

        try {
            BpSeq.fromString(content);
            return InputType.BPSEQ;
        } catch (final IllegalArgumentException ignored) {
            // do nothing
        }

        try {
            Ct.fromString(content);
            return InputType.CT;
        } catch (final IllegalArgumentException ignored) {
            // do nothing
        }

        try {
            DefaultDotBracket.fromString(content);
            return InputType.DOT_BRACKET;
        } catch (final IllegalArgumentException ignored) {
            // do nothing
        }

        throw new IllegalArgumentException("Failed to detect file type");
    }

    public StructureType getStructureType() {
        return structureType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", name(), structureType);
    }
}
