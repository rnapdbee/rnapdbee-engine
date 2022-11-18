package pl.poznan.put.rnapdbee.engine.calculation.testhelp.shared.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class that contains compressed connections
public class Region {

    public final int ID;
    public final int start;
    public final int end;
    public final int length;
    private final List<Pair> contain;
    private boolean isRemoved;

    // Create a region
    public Region(final List<? extends Pair> currentRegions, final int regionID) {
        super();
        contain = new ArrayList<>(currentRegions);
        start = currentRegions.get(0).getFirst();
        end = currentRegions.get(0).getSecond();
        length = currentRegions.size();
        ID = regionID;
        isRemoved = false;
    }

    // Create Array of Regions from connections stored in RNAStructure
    public static List<Region> Regions(final RNAStructure structure) {

        final List<Region> regions = new ArrayList<>();
        final List<Pair> currentRegion = new ArrayList<>();
        int regionID = -1;

        for (int i = 0; i < structure.connections.size(); i++) {
            if (currentRegion.isEmpty()) {
                currentRegion.add(structure.connections.get(i));
            } else {
                if (structure.connections.get(i).getFirst()
                        == currentRegion.get(currentRegion.size() - 1).getFirst() + 1
                        && structure.connections.get(i).getSecond()
                        == currentRegion.get(currentRegion.size() - 1).getSecond() - 1) {
                    currentRegion.add(structure.connections.get(i));
                } else {
                    regionID++;
                    regions.add(new Region(currentRegion, regionID));
                    currentRegion.clear();
                    currentRegion.add(structure.connections.get(i));
                }
            }
        }
        if (!currentRegion.isEmpty()) {
            regionID++;
            regions.add(new Region(currentRegion, regionID));
        }

        return regions;
    }

    public final List<Pair> getPairs() {
        return Collections.unmodifiableList(contain);
    }

    public final boolean isRemoved() {
        return isRemoved;
    }

    public final void remove() {
        isRemoved = true;
    }

    public final void restore() {
        isRemoved = false;
    }
}
