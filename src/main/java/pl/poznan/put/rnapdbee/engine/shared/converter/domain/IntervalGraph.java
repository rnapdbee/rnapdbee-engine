package pl.poznan.put.rnapdbee.engine.shared.converter.domain;

import pl.poznan.put.structure.formats.BpSeq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntervalGraph {

    private final List<Node> nodes;
    private final List<Set<Integer>> edges;

    public IntervalGraph(BpSeq bpseq) {
        nodes = new ArrayList<>();
        /* Create nodes **/
        int nodeNum = 0;
        List<BpSeq.Entry> entries = new ArrayList<>(bpseq.entries());
        int bpseqSize = bpseq.size();
        for (int i = 0; i < bpseqSize; i++) {
            /* Arc merging */
            while (i < bpseqSize && (entries.get(i).pair() - 1 == -1 || (entries.get(i).pair() - 1 != -1 && entries.get(i).pair() - 1 < i))) {
                i++;
            }
            if (i >= bpseqSize) break;
            int j = i;
            while ((j < bpseqSize) && (entries.get(j + 1).pair() - 1 != -1) && (entries.get(j).pair() - 1 == (entries.get(j + 1).pair() - 1 + 1)))
                j++;
            nodes.add(new Node(nodeNum++, i, entries.get(i).pair() - 1, j - i + 1));
            i = j;
        }

        /* Add edges **/
        edges = new ArrayList<>(nodeNum);
        for (int i = 0; i < nodeNum; ++i) {
            edges.add(new HashSet<>());
        }
        for (int i = 0; i < nodes.size(); ++i) {
            for (int j = i + 1; j < nodes.size(); ++j) {
                /* node[i] will always start before node[j], therefore nodes will overlap when:**/
                if (nodes.get(j).getStart() < nodes.get(i).getEnd() && nodes.get(i).getEnd() < nodes.get(j).getEnd()) {
                    edges.get(i).add(j);
                    edges.get(j).add(i);
                }
            }
        }
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Set<Integer>> getEdges() {
        return edges;
    }

    public boolean nodeHasNoEdges(int nodeIndex) {
        return this.edges.get(nodeIndex).size() == 0;
    }
}
