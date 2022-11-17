package pl.poznan.put.rnapdbee.engine.shared.gurobi;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.DefaultDotBracket;
import pl.poznan.put.structure.formats.DotBracket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GurobiSolver {

    static int[] WEIGTHS = {10, -1, -2, -3, -4, -5, -6, -7, -8, -9};
    static int MAX_BRACKET = 10;

    public static void main(String[] args) {
        IntervalGraph intervalGraph = new IntervalGraph(TEST_BP_SEQ);
        try {

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
            char[] bracketing = new char[TEST_BP_SEQ.size()];
            Arrays.fill(bracketing, '.');
            for (Node node : intervalGraph.getNodes()) {
                for (int i = 0; i < node.weight; i++) {
                    bracketing[node.start + i] = getStartingBracket(node.bracketing);
                    bracketing[node.end - i] = getEndingBracket(node.bracketing);
                }
            }

            // print bracketing
            System.out.println(">strand");
            TEST_BP_SEQ.entries().forEach(entry -> System.out.print(entry.seq()));
            System.out.println();
            System.out.println(bracketing);

            StringBuilder dotBracketBuilder = new StringBuilder();
            dotBracketBuilder.append(">strand\n");
            TEST_BP_SEQ.entries().forEach(entry -> dotBracketBuilder.append(entry.seq()));
            dotBracketBuilder.append("\n");
            dotBracketBuilder.append(bracketing);
            dotBracketBuilder.append("\n");
            // TODO: return dotBracket
            DotBracket dotBracket = DefaultDotBracket.fromString(dotBracketBuilder.toString());

        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " +
                    e.getMessage());
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

    private static final BpSeq TEST_BP_SEQ = BpSeq.fromString(
            "1 g 70\n" +
                    "2 C 69\n" +
                    "3 G 68\n" +
                    "4 C 67\n" +
                    "5 C 66\n" +
                    "6 G 65\n" +
                    "7 C 64\n" +
                    "8 U 14\n" +
                    "9 G 0\n" +
                    "10 G 24\n" +
                    "11 U 23\n" +
                    "12 G 22\n" +
                    "13 U 21\n" +
                    "14 A 8\n" +
                    "15 G 46\n" +
                    "16 U 57\n" +
                    "17 G 53\n" +
                    "18 G 54\n" +
                    "19 U 0\n" +
                    "20 A 0\n" +
                    "21 U 13\n" +
                    "22 C 12\n" +
                    "23 A 11\n" +
                    "24 U 10\n" +
                    "25 G 43\n" +
                    "26 C 42\n" +
                    "27 A 41\n" +
                    "28 A 40\n" +
                    "29 G 39\n" +
                    "30 A 38\n" +
                    "31 U 37\n" +
                    "32 U 0\n" +
                    "33 C 0\n" +
                    "34 C 0\n" +
                    "35 C 0\n" +
                    "36 A 0\n" +
                    "37 U 31\n" +
                    "38 U 30\n" +
                    "39 C 29\n" +
                    "40 U 28\n" +
                    "41 U 27\n" +
                    "42 G 26\n" +
                    "43 C 25\n" +
                    "44 G 0\n" +
                    "45 A 0\n" +
                    "46 C 15\n" +
                    "47 C 63\n" +
                    "48 C 62\n" +
                    "49 G 61\n" +
                    "50 G 60\n" +
                    "51 G 59\n" +
                    "52 U 56\n" +
                    "53 U 17\n" +
                    "54 C 18\n" +
                    "55 G 0\n" +
                    "56 A 52\n" +
                    "57 U 16\n" +
                    "58 U 0\n" +
                    "59 C 51\n" +
                    "60 C 50\n" +
                    "61 C 49\n" +
                    "62 G 48\n" +
                    "63 G 47\n" +
                    "64 G 7\n" +
                    "65 C 6\n" +
                    "66 G 5\n" +
                    "67 G 4\n" +
                    "68 C 3\n" +
                    "69 G 2\n" +
                    "70 C 1\n" +
                    "71 A 0\n" +
                    "72 C 0\n" +
                    "73 C 0\n" +
                    "74 A 0\n");

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

//        void print(){
//            cout << "Nodes:" << endl;
//            for(auto n : nodes){
//                cout << n.id << " " << n.start << " " << n.end << " " << n.weight << endl;
//            }
//            cout << "Edges:" << endl;
//            for(int i = 0; i < edges.size(); ++i){
//                cout << i << ": ";
//                for(auto e : edges[i]){
//                    cout << e << " ";
//                }
//                cout << endl;
//            }
//        }

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

            /* No arc merging
            if(bpseq.pair[i] != -1 && bpseq.pair[i] > i){
                nodes.push_back(Node(node_num++, i, bpseq.pair[i], 1));
            }
            */
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
