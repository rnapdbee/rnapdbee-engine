package pl.poznan.put.rnapdbee.engine.shared.converter;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import org.springframework.stereotype.Service;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.DefaultDotBracket;
import pl.poznan.put.structure.formats.DotBracket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class implementing converting BpSeq structure into DotBracket structure using Mixed Integer Linear Programming approach.
 * The implementation is written with Gurobi optimizer.
 */
@Service
public class MixedIntegerLinearProgrammingConverter implements Converter {

    static int[] WEIGTHS = {10, -1, -2, -3, -4, -5, -6, -7, -8, -9};
    static int MAX_BRACKET = 10;

    @Override
    public DotBracket convert(BpSeq bpSeq) {
        try {
            IntervalGraph intervalGraph = new IntervalGraph(bpSeq);
            List<Integer> component = new ArrayList<>();
            int[] position = new int[intervalGraph.getNodeNum()];
            HashSet<Integer> visited = new HashSet<>();

            for (int nodeIndex = 0; nodeIndex < intervalGraph.getNodeNum(); ++nodeIndex) {

                if (visited.contains(nodeIndex)) {
                    continue;
                }

                if (intervalGraph.edges.get(nodeIndex).size() == 0) {
                    visited.add(nodeIndex);
                    intervalGraph.nodes.get(nodeIndex).setBracketing(0);
                    continue;
                }

                depthFirstSearch(nodeIndex, intervalGraph, visited, component);

                for (int componentIndex = 0; componentIndex < component.size(); componentIndex++) {
                    position[component.get(componentIndex)] = componentIndex;
                }

                System.out.println("start gurobi");
                GRBEnv env = new GRBEnv(true);
                env.set("logFile", "mip1.log");
                env.start();
                GRBModel model = new GRBModel(env);

                /* create variables */
                List<GRBVar> variables = component.stream()
                        .flatMap(integer -> IntStream.range(0, MAX_BRACKET)
                                .mapToObj(bracketIndex -> {
                                    String name = String.format("n_%s_t_%s", integer, bracketIndex);
                                    try {
                                        return model.addVar(0.0, 1.0, 0.0, GRB.BINARY, name);
                                    } catch (GRBException e) {
                                        throw new RuntimeException(e);
                                    }
                                }))
                        .collect(Collectors.toList());

                /* create objective function */
                GRBLinExpr expr = new GRBLinExpr();
                for (int i = 0; i < component.size(); ++i) {
                    for (int j = 0; j < MAX_BRACKET; ++j) {
                        GRBVar correspondingVariable = variables.get(i * MAX_BRACKET + j);
                        expr.addTerm(
                                WEIGTHS[j] * intervalGraph.getNodes().get(component.get(i)).weight,
                                correspondingVariable);
                    }
                }
                model.setObjective(expr, GRB.MAXIMIZE);


                /* create each arc one type constraint */
                for (int i = 0; i < component.size(); ++i) {
                    GRBLinExpr constraintExpression = new GRBLinExpr();
                    String name = String.format("c_arc_type_%s", i);
                    for (int j = 0; j < MAX_BRACKET; ++j) {
                        GRBVar correspondingVariable = variables.get(i * MAX_BRACKET + j);
                        constraintExpression.addTerm(1.0, correspondingVariable);
                    }
                    model.addConstr(constraintExpression, GRB.EQUAL, 1.0, name);
                }

                /* create arcs can not cross constraint */
                for (int i = 0; i < component.size(); ++i) {
                    for (int k = 0; k < MAX_BRACKET; k++) {
                        for (int j : intervalGraph.getEdges().get(component.get(i))) {
                            String name = String.format("c_arc_cross_%s_%s_t_%s", i, j, k);
                            GRBVar firstArc = variables.get(i * MAX_BRACKET + k);
                            GRBVar secondArc = variables.get(position[j] * MAX_BRACKET + k);
                            GRBLinExpr constraintExpression = new GRBLinExpr();
                            constraintExpression.addTerm(1.0, firstArc);
                            constraintExpression.addTerm(1.0, secondArc);
                            model.addConstr(constraintExpression, GRB.LESS_EQUAL, 1.0, name);
                        }
                    }
                }
                model.optimize();

                // set bracketing
                for (int i = 0; i < component.size(); ++i) {
                    for (int j = 0; j < MAX_BRACKET; ++j) {
                        if (variables.get(i * MAX_BRACKET + j).get(GRB.DoubleAttr.X) != 0.0) {
                            intervalGraph.getNodes().get(component.get(i)).setBracketing(j);
                        }
                    }
                }

                /* free the resources */
                model.dispose();
                env.dispose();
            }
            // create bracketing
            char[] bracketing = new char[bpSeq.size()];
            Arrays.fill(bracketing, '.');
            for (Node node : intervalGraph.getNodes()) {
                for (int i = 0; i < node.weight; i++) {
                    bracketing[node.start + i] = getStartingBracket(node.bracketing);
                    bracketing[node.end - i] = getEndingBracket(node.bracketing);
                }
            }

            StringBuilder dotBracketBuilder = new StringBuilder();
            dotBracketBuilder.append(">strand\n");
            bpSeq.entries().forEach(entry -> dotBracketBuilder.append(entry.seq()));
            dotBracketBuilder.append("\n");
            dotBracketBuilder.append(bracketing);
            dotBracketBuilder.append("\n");

            return DefaultDotBracket.fromString(dotBracketBuilder.toString());
        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " +
                    e.getMessage());
            throw new RuntimeException();
        }
    }

    private static void depthFirstSearch(int i,
                                         IntervalGraph intervalGraph,
                                         HashSet<Integer> visited,
                                         List<Integer> component) {
        visited.add(i);
        component.add(i);
        intervalGraph.getEdges().get(i).forEach(edge -> {
            if (!visited.contains(edge)) {
                depthFirstSearch(edge, intervalGraph, visited, component);
            }
        });
    }

    private static class Node {
        private int id;
        private int start;
        private int end;
        private int weight;
        private int bracketing;

        public Node(int id, int start, int end, int weight) {
            this.id = id;
            this.start = start;
            this.end = end;
            this.weight = weight;
            this.bracketing = -1;
        }

        public int getId() {
            return id;
        }

        public Node setId(int id) {
            this.id = id;
            return this;
        }

        public int getStart() {
            return start;
        }

        public Node setStart(int start) {
            this.start = start;
            return this;
        }

        public int getEnd() {
            return end;
        }

        public Node setEnd(int end) {
            this.end = end;
            return this;
        }

        public int getWeight() {
            return weight;
        }

        public Node setWeight(int weight) {
            this.weight = weight;
            return this;
        }

        public int getBracketing() {
            return bracketing;
        }

        public void setBracketing(int bracketing) {
            this.bracketing = bracketing;
        }
    }

    private static class IntervalGraph {
        List<Node> nodes;
        List<Set<Integer>> edges;
        int nodeNum;

        // TODO: rewrite to static factory method instead of constructor, clean up
        IntervalGraph(BpSeq bpseq) {
            nodes = new ArrayList<>();
            /* Create nodes **/
            nodeNum = 0;
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
                    if (nodes.get(j).getStart() < nodes.get(i).getEnd() && nodes.get(i).end < nodes.get(j).end) {
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

        public int getNodeNum() {
            return nodeNum;
        }
    }

    static char getStartingBracket(int i) {
        switch (i) {
            case 0:
                return '(';
            case 1:
                return '[';
            case 2:
                return '{';
            case 3:
                return '<';
            case 4:
                return 'A';
            case 5:
                return 'B';
            case 6:
                return 'C';
            case 7:
                return 'D';
            case 8:
                return 'E';
            case 9:
                return 'F';
        }
        return 'Z';
    }

    static char getEndingBracket(int i) {
        switch (i) {
            case 0:
                return ')';
            case 1:
                return ']';
            case 2:
                return '}';
            case 3:
                return '>';
            case 4:
                return 'a';
            case 5:
                return 'b';
            case 6:
                return 'c';
            case 7:
                return 'd';
            case 8:
                return 'e';
            case 9:
                return 'f';
        }
        return 'z';
    }
}
