package pl.poznan.put.rnapdbee.engine.shared.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class KnotRemoval {

    private static final char[] openingBracket = {
            '(', '[', '{', '<', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    private static final char[] closingBracket = {
            ')', ']', '}', '>', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private KnotRemoval() {
        super();
    }

    // New Dynamic Programmig.
    private static void GetResults(
            final int p,
            final int k,
            final int[] beginnings,
            final int[] dome_score,
            final int[] prefix_score,
            final boolean make_results) {
        prefix_score[p] = 0;
        for (int i = p + 1; i < k; i++) {
            prefix_score[i] =
                    beginnings[i] > p
                            && // <- It checks if 'i' is ending of the dome which is in (p, k).
                            dome_score[i] + prefix_score[beginnings[i]] > prefix_score[i - 1]
                            ? dome_score[i] + prefix_score[beginnings[i]]
                            : prefix_score[i - 1];
        }
        if (make_results) dome_score[k] = prefix_score[k - 1] + 1;
    }

    private static List<List<Pair>> MultiplicateVectors(
            final List<List<Pair>> a, final List<List<Pair>> b) {
        // Optimize for empty vectors.
        if (a.get(0).isEmpty()) return b;
        if (b.get(0).isEmpty()) return a;

        // Optimize for single domes.
        if (a.get(0).size() == 1) {
            final Pair to_add = a.get(0).get(0);
            for (final List<Pair> pairs : b) pairs.add(to_add);
            return b;
        }
        if (b.get(0).size() == 1) {
            final Pair to_add = b.get(0).get(0);
            for (final List<Pair> pairs : a) pairs.add(to_add);
            return a;
        }
        final List<List<Pair>> merged = new ArrayList<>();
        for (final Collection<Pair> pairArrayList : a) {
            for (final Collection<Pair> pairs : b) {
                final List<Pair> mergedV = new ArrayList<>();
                mergedV.addAll(pairArrayList);
                mergedV.addAll(pairs);
                merged.add(mergedV);
            }
        }
        return merged;
    }

    private static List<List<Pair>> MergeVectors(final List<List<Pair>> a, final List<List<Pair>> b) {
        if (a.get(0).isEmpty()) return b;
        if (b.get(0).isEmpty()) return a;
        final List<List<Pair>> merged = new ArrayList<>();
        merged.addAll(a);
        merged.addAll(b);
        return merged;
    }

    private static List<List<Pair>> TraceBack(
            final int p,
            final int k,
            final int[] beginnings,
            final int[] dome_score,
            final int[] prefix_score) {
        final List<List<Pair>> results = new ArrayList<>();
        KnotRemoval.GetResults(p, k, beginnings, dome_score, prefix_score, false);
        for (int i = k - 1; i > p; i--) {
            final boolean first_if = (prefix_score[i] > prefix_score[i - 1]);
            final boolean second_if =
                    (prefix_score[i] == prefix_score[i - 1]
                            && prefix_score[i - 1] == prefix_score[beginnings[i]] + dome_score[i]
                            && beginnings[i] != 0);

            if (first_if) {
                final List<List<Pair>> myself = new ArrayList<>();
                final List<Pair> mypair = new ArrayList<>();
                mypair.add(new Pair(beginnings[i], i));
                myself.add(mypair);

                return KnotRemoval.MultiplicateVectors(
                        KnotRemoval.MultiplicateVectors(
                                myself,
                                KnotRemoval.TraceBack(beginnings[i], i, beginnings, dome_score, prefix_score)),
                        KnotRemoval.TraceBack(p, beginnings[i], beginnings, dome_score, prefix_score));
            } else if (second_if) {
                final List<List<Pair>> myself = new ArrayList<>();
                final List<Pair> mypair = new ArrayList<>();
                mypair.add(new Pair(beginnings[i], i));
                myself.add(mypair);

                final List<List<Pair>> take =
                        KnotRemoval.MultiplicateVectors(
                                KnotRemoval.MultiplicateVectors(
                                        myself,
                                        KnotRemoval.TraceBack(beginnings[i], i, beginnings, dome_score, prefix_score)),
                                KnotRemoval.TraceBack(p, beginnings[i], beginnings, dome_score, prefix_score));
                final List<List<Pair>> skip =
                        KnotRemoval.TraceBack(p, i, beginnings, dome_score, prefix_score);

                return KnotRemoval.MergeVectors(take, skip);
            }
        }
        results.add(new ArrayList<>());
        return results;
    }

    public static List<List<Pair>> dp_new_all(final RNAStructure structure) {
        final RNAStructure str = new RNAStructure(structure);

        final int n = str.rnaSequence.size();
        final int[] beginnings = new int[n + 2];
        final int[] dome_score = new int[n + 2];
        final int[] prefix_score = new int[n + 2];

        for (final Pair p : str.connections) {
            final int lower = Math.min(p.getSecond(), p.getFirst());
            final int upper = Math.max(p.getSecond(), p.getFirst());
            beginnings[upper] = lower;
        }

        // Calculate dome socers.
        for (int i = 1; i <= n; ++i)
            if (beginnings[i] > 0)
                KnotRemoval.GetResults(beginnings[i], i, beginnings, dome_score, prefix_score, true);
        KnotRemoval.GetResults(0, n + 1, beginnings, dome_score, prefix_score, true);

        // Track back pairs.

        return KnotRemoval.TraceBack(0, n + 1, beginnings, dome_score, prefix_score);
    }

    public static RNAStructure dynamicProgrammingOneBest(final RNAStructure structure) {
        return KnotRemoval.dynamicProgrammingAllBest(structure).get(0);
    }

    public static List<RNAStructure> dynamicProgrammingAllBest(final RNAStructure structure) {
        final Iterable<RNAStructure> all_results = KnotRemoval.dynamicProgrammingAll(structure);
        final List<RNAStructure> best_results = new ArrayList<>();
        int min_order = 1000;
        for (final RNAStructure str : all_results) min_order = Math.min(min_order, str.order);
        for (final RNAStructure str : all_results) if (str.order == min_order) best_results.add(str);

        return best_results;
    }

    public static List<RNAStructure> dynamicProgrammingAll(final RNAStructure structure) {
        // Deep copy for RNAStructure.
        final RNAStructure str_original = new RNAStructure(structure);

        // Add full structure as first result.
        List<RNAStructure> results = new ArrayList<>();
        results.add(str_original);

        int bracket = 0;
        while (true) {
            results = KnotRemoval.GetOneOrder(results, bracket);
            bracket++;
            if (KnotRemoval.isFinished(results)) return results;
        }
    }

    public static boolean isFinished(final Iterable<? extends RNAStructure> structures) {
        for (final RNAStructure str : structures) if (!str.finished) return false;
        return true;
    }

    public static List<RNAStructure> GetOneOrder(
            final Iterable<? extends RNAStructure> structures, final int bracket) {

        final List<RNAStructure> results = new ArrayList<>();
        for (final RNAStructure structure : structures) {
            // Add structures that are finished and dont process them again.
            if (structure.finished) {
                results.add(structure);
                continue;
            }
            // Get results for current structures.
            final Iterable<List<Pair>> correct_connections = KnotRemoval.dp_new_all(structure);

            // Create new RNAStructure for every result.
            for (final List<Pair> correct_connection : correct_connections) {
                // Make a deep copy, update correct connections, remove used.
                final RNAStructure str = new RNAStructure(structure);
                str.DP = true;

                KnotRemoval.removeCorrect(str, correct_connection);
                KnotRemoval.updateConnectionPairs(str);
                KnotRemoval.updateDotBracket(str, bracket);
                str.order = bracket;
                if (correct_connection.isEmpty())
                    str.finished = true; // If there are no pairs to add it is finished.
                results.add(str);
            }
        }
        return results;
    }

    private static void removeCorrect(
            final RNAStructure str, final Iterable<? extends Pair> correct) {
        str.correctConnections =
                str.originalConnections.stream()
                        .mapToInt(i -> i)
                        .mapToObj(i -> -1)
                        .collect(Collectors.toCollection(ArrayList::new));
        for (final Pair pair : correct) {
            final int beginning = pair.getFirst();
            final int ending = pair.getSecond();
            str.originalConnections.set(beginning - 1, -1);
            str.originalConnections.set(ending - 1, -1);
            str.correctConnections.set(beginning - 1, ending - 1);
            str.correctConnections.set(ending - 1, beginning - 1);
        }
    }

    // Dynamic Programming
    public static RNAStructure dynamicProgramming(final RNAStructure structure) {
        // Create a deep copy of RNAStructure
        final RNAStructure str = new RNAStructure(structure);
        str.DP = true;
        int bracket = -1;
        while (KnotRemoval.havePairs(str)) {
            ++bracket;
            final Map<Integer, Integer> idMap = new HashMap<>();
            List<Integer> connections = KnotRemoval.filterNotConnected(str.originalConnections, idMap);

            connections = KnotRemoval.addOuterRange(connections);
            final int size = connections.size();

            // Create array of ranges and sort them
            final List<Range> ranges = KnotRemoval.createRanges(connections);
            Collections.sort(ranges);

            // Initialize arrays needed for dynamic programming (it takes approximately 2/3 of program
            // execution time)
            final int[][] dpScore = new int[size][size];
            final int[][] dpPosition = new int[size][size];
            for (int i = 0; i < size; i++) for (int j = 0; j < size; j++) dpPosition[i][j] = -1;

            // Fill dp array for results
            for (final Range range : ranges) {
                final int[] prefixesScore = dpScore[range.left + 1];
                final int[] prefixesPosition = dpPosition[range.left + 1];

                for (int pos = range.left + 1; pos < range.right; pos++) {
                    final int paired = connections.get(pos);

                    if (pos == range.left + 1) continue;

                    if (!range.contains(paired) || (paired > pos)) {
                        if (prefixesScore[pos] < prefixesScore[pos - 1]) {
                            prefixesScore[pos] = prefixesScore[pos - 1];
                            prefixesPosition[pos] = prefixesPosition[pos - 1];
                        }
                    } else if (paired < pos) {
                        if (prefixesScore[pos - 1] < (prefixesScore[paired - 1] + dpScore[paired][pos])
                                && prefixesScore[pos] < (prefixesScore[paired - 1] + dpScore[paired][pos])) {
                            prefixesScore[pos] = prefixesScore[paired - 1] + dpScore[paired][pos];
                            prefixesPosition[pos] = pos;
                        } else if (prefixesScore[pos] < prefixesScore[pos - 1]) {
                            prefixesScore[pos] = prefixesScore[pos - 1];
                            prefixesPosition[pos] = prefixesPosition[pos - 1];
                        }
                    }
                }
                dpScore[range.left][range.right] = 1 + prefixesScore[range.right - 1];
                dpPosition[range.left][range.right] = range.right;
            }

            final List<Integer> correctConnections = new ArrayList<>();
            KnotRemoval.appendCorrectConnections(
                    1, size - 2, connections, dpPosition, correctConnections);

            List<Integer> nonConflicting = KnotRemoval.keepSelected(connections, correctConnections);
            nonConflicting = KnotRemoval.removeOuterRange(nonConflicting);

            str.correctConnections = KnotRemoval.mapBack(str.originalConnections, nonConflicting, idMap);

            KnotRemoval.updateDotBracket(str, bracket);
            KnotRemoval.removeUsedConnections(str);
        }

        return str;
    }

    // Elimination Gain
    public static RNAStructure eliminationGain(final RNAStructure structure) {

        // Make a deep copy of processing structure
        final RNAStructure str = new RNAStructure(structure);
        int bracket = -1;
        while (KnotRemoval.havePairs(str)) {
            ++bracket;

            // Initialize all variables for Elimination Gain function
            final List<Region> regions = new ArrayList<>(KnotRemoval.createRegions(str));
            final Map<Integer, Region> idToRegion = KnotRemoval.createMap(regions);
            final ConflictMap cm = new ConflictMap(idToRegion);
            List<Integer> conflicts = cm.conflicting();
            final List<Region> removed = new ArrayList<>();

            int idToRemove;

            // Main loop with function
            while (!conflicts.isEmpty()) {
                idToRemove = KnotRemoval.findMinGain(conflicts, cm, idToRegion);
                removed.add(idToRegion.get(idToRemove));
                regions.get(idToRemove).remove();
                idToRegion.remove(idToRemove);
                cm.remove(idToRemove);
                conflicts = cm.conflicting();
            }

            // Check if removed regions are still conflicting, if not add them back
            final Collection<Region> addBackRegions =
                    new ArrayList<>(KnotRemoval.addBackNonConflicting(idToRegion, removed));

            regions.addAll(addBackRegions);

            // Change Regions to pairs
            str.connections = KnotRemoval.toPairs(regions);

            KnotRemoval.updateConnectionsFromPairs(str);
            KnotRemoval.updateDotBracket(str, bracket);
            KnotRemoval.removeUsedConnections(str);
            KnotRemoval.updateConnectionPairs(str);
        }

        return str; // new RNAStructure(structure.connections, structure.rnaSequence);
    }

    // Elimination Conflict
    public static RNAStructure eliminationConflict(final RNAStructure structure) {

        // Make a deep copy of processing structure
        final RNAStructure str = new RNAStructure(structure);
        int bracket = -1;
        while (KnotRemoval.havePairs(str)) {
            ++bracket;

            // Initialize all variables for Elimination Gain function
            final List<Region> regions = new ArrayList<>(KnotRemoval.createRegions(str));
            final Map<Integer, Region> idToRegion = KnotRemoval.createMap(regions);
            final ConflictMap cm = new ConflictMap(idToRegion);
            List<Integer> conflicts = cm.conflicting();
            final List<Region> removed = new ArrayList<>();

            int idToRemove;

            // Main loop with function
            while (!conflicts.isEmpty()) {
                idToRemove = KnotRemoval.findMaxConflict(conflicts, cm, idToRegion);
                removed.add(idToRegion.get(idToRemove));
                regions.get(idToRemove).remove();
                idToRegion.remove(idToRemove);
                cm.remove(idToRemove);
                conflicts = cm.conflicting();
            }

            // Check if removed regions are still conflicting, if not add them back
            final Collection<Region> addBackRegions =
                    new ArrayList<>(KnotRemoval.addBackNonConflicting(idToRegion, removed));

            regions.addAll(addBackRegions);

            // Change Regions to pairs
            str.connections = KnotRemoval.toPairs(regions);

            KnotRemoval.updateConnectionsFromPairs(str);
            KnotRemoval.updateDotBracket(str, bracket);
            KnotRemoval.removeUsedConnections(str);
            KnotRemoval.updateConnectionPairs(str);
        }

        return str; // new RNAStructure(structure.connections, structure.rnaSequence);
    }
    ///////////////////////////////////
    // Elimination Gain / Conflict functions
    ///////////////////////////////////

    // Create Array of Regions from connections stored in RNAStructure
    private static List<Region> createRegions(final RNAStructure structure) {

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

    // Create map of Regions
    private static Map<Integer, Region> createMap(final Iterable<? extends Region> regions) {
        final Map<Integer, Region> idToRegion = new HashMap<>();

        for (final Region region : regions) {
            idToRegion.put(region.ID, region);
        }

        return idToRegion;
    }

    public static List<Pair> toPairs(final Iterable<? extends Region> regions) {
        final List<Pair> connections = new ArrayList<>();
        int start, end;

        for (final Region region : regions) {
            if (!region.isRemoved()) {
                start = region.start;
                end = region.end;
                for (int j = 0; j < region.length; j++) {
                    connections.add(new Pair(start + j, end - j));
                }
            }
        }

        return connections;
    }

    // Rewritten find_min_gain function from PyCogent-1.5.3
    private static Integer findMinGain(
            final Iterable<Integer> conflicts,
            final ConflictMap cm,
            final Map<Integer, ? extends Region> idToRegion) {
        int id,
                regionLength,
                conflictLength,
                lenDiff,
                lenDiffMin = 2000000000,
                noc,
                nocMax = -100,
                start,
                startMax = -100;
        Iterable<Integer> conflictsWith;
        final SortedMap<Integer, List<Integer>> lenDiffs = new TreeMap<>();
        final SortedMap<Integer, List<Integer>> numberOfConflicts = new TreeMap<>();
        final SortedMap<Integer, Integer> startValues = new TreeMap<>();

        for (final Integer conflict : conflicts) {
            id = conflict;
            regionLength = idToRegion.get(id).length;
            conflictLength = 0;
            conflictsWith = cm.conflictsWith(id);
            for (final Integer integer : conflictsWith) {
                conflictLength += idToRegion.get(integer).length;
            }
            lenDiff = regionLength - conflictLength;
            if (!lenDiffs.containsKey(lenDiff)) lenDiffs.put(lenDiff, new ArrayList<>());
            lenDiffs.get(lenDiff).add(id);
            lenDiffMin = Math.min(lenDiff, lenDiffMin);
        }

        final List<Integer> minIDs = new ArrayList<>(lenDiffs.get(lenDiffMin));
        if (minIDs.size() == 1) {
            return minIDs.get(0);
        } else {
            for (final Integer integer : minIDs) {
                id = integer;
                noc = cm.conflictsWith(id).size();
                if (!numberOfConflicts.containsKey(noc)) numberOfConflicts.put(noc, new ArrayList<>());
                numberOfConflicts.get(noc).add(id);
                nocMax = Math.max(nocMax, noc);
            }
            final List<Integer> maxIDs = new ArrayList<>(numberOfConflicts.get(nocMax));

            if (maxIDs.size() == 1) {
                return maxIDs.get(0);
            } else {
                for (final Integer minID : minIDs) {
                    start = idToRegion.get(minID).start;
                    startValues.put(start, minID);
                    startMax = Math.max(startMax, start);
                }
                return startValues.get(startMax);
            }
        }
    }

    // Rewritten find_max_conflict function from PyCogent-1.5.3
    private static Integer findMaxConflict(
            final Iterable<Integer> conflicts,
            final ConflictMap cm,
            final Map<Integer, ? extends Region> idToRegion) {
        int id,
                regionLength,
                conflictLength,
                lenDiff,
                lenDiffMin = 2000000000,
                noc,
                nocMax = -100,
                start,
                startMax = -100;
        Iterable<Integer> conflictsWith;
        final SortedMap<Integer, List<Integer>> lenDiffs = new TreeMap<>();
        final SortedMap<Integer, List<Integer>> numberOfConflicts = new TreeMap<>();
        final SortedMap<Integer, Integer> startValues = new TreeMap<>();

        for (final Integer conflict : conflicts) {
            id = conflict;
            noc = cm.conflictsWith(id).size();
            if (!numberOfConflicts.containsKey(noc)) numberOfConflicts.put(noc, new ArrayList<>());
            numberOfConflicts.get(noc).add(id);
            nocMax = Math.max(nocMax, noc);
        }
        final List<Integer> maxIDs = new ArrayList<>(numberOfConflicts.get(nocMax));
        if (maxIDs.size() == 1) {
            return maxIDs.get(0);
        } else {

            for (final Integer maxID : maxIDs) {
                id = maxID;
                regionLength = idToRegion.get(id).length;
                conflictLength = 0;
                conflictsWith = cm.conflictsWith(id);
                for (final Integer integer : conflictsWith) {
                    conflictLength += idToRegion.get(integer).length;
                }
                lenDiff = regionLength - conflictLength;
                if (!lenDiffs.containsKey(lenDiff)) lenDiffs.put(lenDiff, new ArrayList<>());
                lenDiffs.get(lenDiff).add(id);
                lenDiffMin = Math.min(lenDiff, lenDiffMin);
            }
            final List<Integer> minIDs = new ArrayList<>(lenDiffs.get(lenDiffMin));

            if (minIDs.size() == 1) {
                return minIDs.get(0);
            } else {
                for (final Integer minID : minIDs) {
                    start = idToRegion.get(minID).start;
                    startValues.put(start, minID);
                    startMax = Math.max(startMax, start);
                }
                return startValues.get(startMax);
            }
        }
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

    // Returns Array of Regions that were removed but can be added back because the are not
    // conflicting anymore
    private static List<Region> addBackNonConflicting(
            final Map<? super Integer, Region> idToRegion, final List<? extends Region> removed) {
        final List<Region> addBack = new ArrayList<>();
        final SortedMap<Integer, Integer> order = new TreeMap<>();
        final Map<Integer, Region> idToRemovedRegion = KnotRemoval.createMap(removed);
        boolean added = true, conflicting;

        for (final Region region : removed) {
            order.put(region.start, region.ID);
        }

        while (added) {
            added = false;
            for (final Map.Entry<Integer, Integer> entry : order.entrySet()) {
                conflicting =
                        idToRegion.values().stream()
                                .anyMatch(
                                        pr2 -> KnotRemoval.isConflicting(idToRemovedRegion.get(entry.getValue()), pr2));
                if (!conflicting) {
                    idToRegion.put(entry.getValue(), idToRemovedRegion.get(entry.getValue()));
                    idToRemovedRegion.get(entry.getValue()).restore();
                    addBack.add(idToRemovedRegion.get(entry.getValue()));
                    idToRemovedRegion.remove(entry.getValue());
                    for (int k = 0; k < removed.size(); k++) {
                        if (removed.get(k).ID == entry.getValue()) removed.remove(k);
                    }
                    order.remove(entry.getKey());
                    added = true;
                    break;
                }
            }
        }
        return addBack;
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

    // Update correct connections from pairs
    private static void updateConnectionsFromPairs(final RNAStructure str) {
        final List<Integer> correctConnections;
        final int size = str.rnaSequence.size();
        final int[] temp = new int[size + 2];

        for (int i = 0; i < str.connections.size(); i++) {
            temp[str.connections.get(i).getFirst()] = str.connections.get(i).getSecond();
            temp[str.connections.get(i).getSecond()] = str.connections.get(i).getFirst();
        }
        correctConnections =
                IntStream.range(0, str.rnaSequence.size())
                        .mapToObj(i -> temp[i + 1])
                        .collect(Collectors.toCollection(ArrayList::new));
        str.correctConnections = correctConnections;
    }

    /////////////////////////////////////
    // Dynamic Programming functions
    /////////////////////////////////////

    // Create idMap to access connections and Array of connections
    private static List<Integer> filterNotConnected(
            final List<Integer> originalConnections, final Map<? super Integer, ? super Integer> idMap) {
        final List<Integer> filtered = new ArrayList<>();
        int id = 0;
        final Map<Integer, Integer> originalToFiltered = new HashMap<>();
        for (int i = 0; i < originalConnections.size(); i++) {
            if (originalConnections.get(i) != -1) {
                originalToFiltered.put(i, id);
                idMap.put(id, i);
                id++;
                filtered.add(originalConnections.get(i));
            }
        }

        for (int i = 0; i < filtered.size(); i++) {
            final int pos = filtered.get(i);
            filtered.set(i, originalToFiltered.get(pos));
        }
        return filtered;
    }

    // Create one range that contains all of the other
    private static List<Integer> addOuterRange(final Collection<Integer> connections) {
        final List<Integer> res = new ArrayList<>();

        res.add(connections.size() + 1);
        for (final Integer connection : connections) {
            res.add(connection + 1);
        }
        res.add(0);

        return res;
    }

    // Create Ranges from given connections
    private static List<Range> createRanges(final List<Integer> connections) {

        return IntStream.range(0, connections.size())
                .filter(i -> i < connections.get(i))
                .mapToObj(i -> new Range(i, connections.get(i)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // If connections is correct add them to final connections array (make final correct connections)
    private static void appendCorrectConnections(
            final int a,
            final int b,
            final List<Integer> connections,
            final int[][] dpPosition,
            final List<? super Integer> appendTo) {
        if (a >= b) return;
        if (dpPosition[a][b] != -1) {
            final int right = dpPosition[a][b];
            final int left = connections.get(right);

            appendTo.add(left);
            appendTo.add(right);

            KnotRemoval.appendCorrectConnections(left + 1, right - 1, connections, dpPosition, appendTo);
            KnotRemoval.appendCorrectConnections(a, left - 1, connections, dpPosition, appendTo);
        }
    }

    // Tell which connections should be as the final ones
    private static List<Integer> keepSelected(
            final List<Integer> connections, final Iterable<Integer> toKeep) {
        final boolean[] shouldKeep = new boolean[connections.size()];
        Arrays.fill(shouldKeep, false);
        for (final int c : toKeep) shouldKeep[c] = true;
        final List<Integer> res = new ArrayList<>(connections);
        for (int i = 0; i < res.size(); i++) {
            if (!shouldKeep[i]) {
                res.set(i, -1);
            }
        }
        return res;
    }

    // Remove helping range that contains everything
    private static List<Integer> removeOuterRange(final List<Integer> connections) {
        final List<Integer> res = new ArrayList<>();

        for (int i = 1; i < connections.size() - 1; i++) {
            if (connections.get(i) == -1) {
                res.add(-1);
            } else {
                res.add(connections.get(i) - 1);
            }
        }

        return res;
    }

    // Build final connections using idMap and filtered connections
    private static List<Integer> mapBack(
            final List<Integer> original,
            final List<Integer> filtered,
            final Map<Integer, Integer> idMap) {
        final List<Integer> result = new ArrayList<>(original);

        for (int i = 0; i < filtered.size(); i++) {
            if (filtered.get(i) == -1) {
                result.set(idMap.get(i), -1);
            }
        }

        return result;
    }

    /////////////////////////////////////
    // Misc functions
    /////////////////////////////////////

    private static boolean havePairs(final RNAStructure structure) {
        int isDP = 0;
        if (structure.DP) isDP = -1;
        for (final int i : structure.originalConnections) if (i != isDP) return true;
        return false;
    }

    private static void updateDotBracket(final RNAStructure str, final int bracket) {
        int isDP = 0;
        if (str.DP) isDP = -1;
        for (int i = 0; i < str.correctConnections.size(); i++) {
            if (str.correctConnections.get(i) > i && str.correctConnections.get(i) != isDP)
                str.DotBracket.set(i, KnotRemoval.openingBracket[bracket]);
            else if (str.correctConnections.get(i) <= i && str.correctConnections.get(i) != isDP)
                str.DotBracket.set(i, KnotRemoval.closingBracket[bracket]);
        }
    }

    private static void removeUsedConnections(final RNAStructure str) {
        int isDP = 1;
        if (str.DP) isDP = 0;
        for (int i = 0; i < str.correctConnections.size(); i++) {
            if (str.originalConnections.get(i) + isDP == str.correctConnections.get(i)) {
                str.originalConnections.set(i, isDP - 1);
                str.correctConnections.set(i, isDP - 1);
            }
        }
    }

    private static void updateConnectionPairs(final RNAStructure str) {
        str.connections.clear();
        for (int i = 0; i < str.originalConnections.size(); i++) {
            if (i < str.originalConnections.get(i))
                str.connections.add(new Pair(i + 1, str.originalConnections.get(i) + 1));
        }
    }
}
