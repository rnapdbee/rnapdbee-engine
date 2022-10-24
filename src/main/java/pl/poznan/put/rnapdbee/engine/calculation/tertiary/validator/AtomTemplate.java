package pl.poznan.put.rnapdbee.engine.calculation.tertiary.validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class AtomTemplate implements Serializable {
    private List<String> atomNames = null;
    private String templateAtomName = null;
    private String originalAtomName = null;

    AtomTemplate(String atNames, String tAtomName) {
        originalAtomName = tAtomName;
        templateAtomName = tAtomName.trim();
        atomNames = new ArrayList<String>();
        String[] elems = atNames.split(",");
        if (elems.length > 0) {
            for (String elem : elems) {
                atomNames.add(elem);
            }
        }
    }

    boolean isProperAtomName(String atomName) {
        String name = atomName.trim().toUpperCase();
        for (int i = 0; i < atomNames.size(); i++) {
            if (name.equals(atomNames.get(i))) {
                return true;
            }
        }
        return false;
    }

    boolean isPossiblyProperAtomName(String atomName) {
        String name = atomName.trim().toUpperCase();
        for (int i = 0; i < atomNames.size(); i++) {
            if (atomNames.get(i).indexOf(name) >= 0) {
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
