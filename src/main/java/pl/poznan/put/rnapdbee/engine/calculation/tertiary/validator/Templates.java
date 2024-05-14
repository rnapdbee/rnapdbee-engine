package pl.poznan.put.rnapdbee.engine.calculation.tertiary.validator;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Whole package copied from rnapdbee 2.0 implementation
 */
public class Templates implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Templates.class);
    private static final Set<String> aminoAcids =
            new HashSet<>(Arrays.asList("ALA", "CYS", "ASP", "GLU", "PHE", "GLY", "HIS", "ILE",
                    "LYS", "LEU", "MET", "ASN", "PRO", "GLN", "ARG", "SER",
                    "THR", "VAL", "TRP", "TYR"));

    private List<ResidueTemplate> templates;

    public Templates(final File path) throws IOException {
        super();
        try (final InputStream stream = new FileInputStream(path)) {
            loadTemplates(stream);
        }
    }

    private void loadTemplates(final InputStream stream) {
        templates = new ArrayList<>();
        ResidueTemplate residue = null;

        for (final String line : IOUtils.readLines(stream, Charset.defaultCharset())) {
            if (line.indexOf(';') >= 0) {
                final String[] elems = line.split(";");
                if (elems.length == 3) {
                    if (residue == null) {
                        residue = new ResidueTemplate(elems[0], elems[1], elems[2]);
                    } else {
                        if (residue.isEqualResidueName(elems[0].split(",")[1])) {
                            residue.addAtomTemplate(elems[1], elems[2]);
                        } else {
                            templates.add(residue);
                            residue = new ResidueTemplate(elems[0], elems[1], elems[2]);
                        }
                    }
                }
            }
        }
        if (residue != null) {
            templates.add(residue);
        }
    }

    public Templates(final InputStream stream) {
        super();
        loadTemplates(stream);
    }

    public String getOneResidueNameByStructuralName(String resName) {
        int index = getResidueIndexByName(resName);
        if (index >= 0) {
            return templates.get(index).getOneResidueName();
        }
        return null;
    }

    private int getResidueIndexByName(String resName) {
        String name = resName.trim().toUpperCase();
        if (name.length() > 1) {
            for (int i = 0; i < templates.size(); i++) {
                if (name.equals(templates.get(i).getThreeResidueName())) {
                    return i;
                }
            }
            int howMany = 0;
            int index = -1;
            for (int i = 0; i < templates.size(); i++) {
                if (name.endsWith(templates.get(i).getOneResidueName())) {
                    howMany++;
                    index = i;
                }
            }
            if (howMany == 1) {
                return index;
            }
            howMany = 0;
            index = -1;
            for (int i = 0; i < templates.size(); i++) {
                if (name.indexOf(templates.get(i).getOneResidueName()) >= 0) {
                    howMany++;
                    index = i;
                }
            }
            if (howMany == 1) {
                return index;
            }
        } else {
            for (int i = 0; i < templates.size(); i++) {
                if (name.equals(templates.get(i).getOneResidueName())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public String getOriginalAtomName(String resName, String atomName) {
        int index = getResidueIndexByName(resName);
        if (index >= 0) {
            return templates.get(index).getOriginalAtomName(atomName);
        }
        return null;
    }

    public String getTemplateAtomName(String resName, String atomName) {
        int index = getResidueIndexByName(resName);
        if (index >= 0) {
            return templates.get(index).getTemplateAtomName(atomName);
        }
        return null;
    }

    public boolean isProperAtomName(String residueName, String atomName) {
        int resIndex = getResidueIndexByName(residueName);
        if (resIndex >= 0) {
            return templates.get(resIndex).isProperAtomName(atomName);
        }
        return false;
    }

    public boolean isProperResidueName(String resName) {
        String name = resName.trim().toUpperCase();
        if (name.length() > 1) {
            if (Templates.aminoAcids.contains(name.toUpperCase())) {
                return false;
            }

            for (int i = 0; i < templates.size(); i++) {
                if (name.equals(templates.get(i).getThreeResidueName())) {
                    return true;
                }
            }
            int howMany = 0;
            for (int i = 0; i < templates.size(); i++) {
                if (name.endsWith(templates.get(i).getOneResidueName())) {
                    howMany++;
                }
            }
            if (howMany == 1) {
                return true;
            }
            howMany = 0;
            for (int i = 0; i < templates.size(); i++) {
                if (name.indexOf(templates.get(i).getOneResidueName()) >= 0) {
                    howMany++;
                }
            }
            if (howMany == 1) {
                return true;
            }
        } else {
            for (int i = 0; i < templates.size(); i++) {
                if (name.equals(templates.get(i).getOneResidueName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getProperResidueNames() {
        StringBuilder builder = new StringBuilder();
        for (ResidueTemplate template : templates) {
            builder.append(template.getThreeResidueName());
            builder.append(' ');
            builder.append(template.getOneResidueName());
        }
        return builder.toString();
    }
}
