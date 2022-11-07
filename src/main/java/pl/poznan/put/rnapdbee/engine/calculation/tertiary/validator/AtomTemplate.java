package pl.poznan.put.rnapdbee.engine.calculation.tertiary.validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class AtomTemplate implements Serializable {
    private final List<String> atomNames;
    private final String templateAtomName;
    private final String originalAtomName;

    AtomTemplate(String atNames, String tAtomName) {
        originalAtomName = tAtomName;
        templateAtomName = tAtomName.trim();
        atomNames = new ArrayList<>();
        String[] elems = atNames.split(",");
        if (elems.length > 0) {
            Collections.addAll(atomNames, elems);
        }
    }

    boolean isProperAtomName(String atomName) {
        String name = atomName.trim().toUpperCase();
        for (String s : atomNames) {
            if (name.equals(s)) {
                return true;
            }
        }
        return false;
    }

    boolean isPossiblyProperAtomName(String atomName) {
        String name = atomName.trim().toUpperCase();
        for (String s : atomNames) {
            if (s.contains(name)) {
                return true;
            }
        }
        return false;
    }

    public String getTemplateAtomName() {
        return templateAtomName;
    }

    public String getOriginalAtomName() {
        return originalAtomName;
    }

}
