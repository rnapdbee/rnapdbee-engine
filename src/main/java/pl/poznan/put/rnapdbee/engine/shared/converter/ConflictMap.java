package pl.poznan.put.rnapdbee.engine.shared.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConflictMap {

    private final Map<Integer, HashMap<Integer, Boolean>> conflicts = new HashMap<>();

    // Create a basic 2DMap from which tells about conflicts. It can return if given Region have
    // conflicts and give these conflicts
    public ConflictMap(final Map<Integer, ? extends Region> idToRegion) {
        super();
        boolean conflict;

        for (int i = 0; i < idToRegion.size(); i++) {
            conflicts.put(i, new HashMap<>());
        }

        for (int i = 0; i < idToRegion.size(); i++) {
            for (int j = i + 1; j < idToRegion.size(); j++) {
                conflict = ConflictMap.isConflicting(idToRegion.get(i), idToRegion.get(j));
                if (conflict) {
                    conflicts.get(i).put(j, true);
                    conflicts.get(j).put(i, true);
                }
            }
        }

        for (int i = 0; i < idToRegion.size(); i++) {
            if (conflicts.get(i).isEmpty()) conflicts.remove(i);
        }
    }

    // Check if given Regions are conflicting
    private static boolean isConflicting(final Region first, final Region second) {
        final int startFirst = first.start;
        final int endFirst = first.end;
        final int startSecond = second.start;
        final int endSecond = second.end;

        return (((startFirst < startSecond) && (endFirst < endSecond) && (startSecond < endFirst))
                || ((startSecond < startFirst) && (endSecond < endFirst) && (startFirst < endSecond)));
    }

    // Return array of ids that are still conflicting
    public final ArrayList<Integer> conflicting() {

        return new ArrayList<>(conflicts.keySet());
    }

    // Remove Region and all associated conflicts with it
    public final void remove(final int idToRemove) {
        int id;
        final Iterable<Integer> conflictingIDs = new ArrayList<>(conflicts.get(idToRemove).keySet());
        for (final Integer conflictingID : conflictingIDs) {
            id = conflictingID;
            conflicts.get(id).remove(idToRemove);
            if (conflicts.get(id).isEmpty()) conflicts.remove(id);
        }
        conflicts.remove(idToRemove);
    }

    // Return all Regions (ID) that conflicts with given Region
    public final List<Integer> conflictsWith(final int id) {
        return new ArrayList<>(conflicts.get(id).keySet());
    }
}
