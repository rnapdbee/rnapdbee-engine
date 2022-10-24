package pl.poznan.put.rnapdbee.engine.calculation.tertiary.validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class ResidueTemplate implements Serializable {
    private String oneResidueName = null;
    private String threeResidueName = null;
    private List<AtomTemplate> atomTemplates = null;

    ResidueTemplate(String residueNames, String atomNames,
                    String templateAtomName) {
        String[] resNames = residueNames.split(",");
        if (resNames.length == 2) {
            oneResidueName = resNames[0];
            threeResidueName = resNames[1];
        }
        atomTemplates = new ArrayList<AtomTemplate>();
        atomTemplates.add(new AtomTemplate(atomNames, templateAtomName));
    }

    void addAtomTemplate(String atomNames, String templateAtomName) {
        atomTemplates.add(new AtomTemplate(atomNames, templateAtomName));
    }

    public String getOneResidueName() {
        return oneResidueName;
    }

    public String getThreeResidueName() {
        return threeResidueName;
    }

    boolean isEqualResidueName(String resName) {
        String name = resName.trim().toUpperCase();
        if (name.equals(threeResidueName) || name.equals(oneResidueName) || name
                .endsWith(oneResidueName)
                || name.indexOf(oneResidueName) >= 0) {
            return true;
        }
        return false;
    }

    boolean isProperAtomName(String atomName) {
        for (int i = 0; i < atomTemplates.size(); i++) {
            if (atomTemplates.get(i).isProperAtomName(atomName)) {
                return true;
            }
        }
        int howMany = 0;
        for (int i = 0; i < atomTemplates.size(); i++) {
            if (atomTemplates.get(i).isPossiblyProperAtomName(atomName)) {
                howMany++;
            }
        }
        if (howMany == 1) {
            return true;
        }
        return false;
    }

    String getTemplateAtomName(String atomName) {
        for (int i = 0; i < atomTemplates.size(); i++) {
            if (atomTemplates.get(i).isProperAtomName(atomName)) {
                return atomTemplates.get(i).getTemplateAtomName();
            }
        }
        int howMany = 0;
        int index = -1;
        for (int i = 0; i < atomTemplates.size(); i++) {
            if (atomTemplates.get(i).isPossiblyProperAtomName(atomName)) {
                howMany++;
                index++;
            }
        }
        if (howMany == 1) {
            return atomTemplates.get(index).getTemplateAtomName();
        }
        return null;
    }

    String getOriginalAtomName(String atomName) {
        for (int i = 0; i < atomTemplates.size(); i++) {
            if (atomTemplates.get(i).isProperAtomName(atomName)) {
                return atomTemplates.get(i).getOriginalAtomName();
            }
        }
        int howMany = 0;
        int index = -1;
        for (int i = 0; i < atomTemplates.size(); i++) {
            if (atomTemplates.get(i).isPossiblyProperAtomName(atomName)) {
                howMany++;
                index++;
            }
        }
        if (howMany == 1) {
            return atomTemplates.get(index).getOriginalAtomName();
        }
        return null;
    }
}
