package pl.poznan.put.rnapdbee.engine.shared.gurobi;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import pl.poznan.put.structure.formats.BpSeq;

import java.util.ArrayList;
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
                                        return model.addVar(0.0, 1.0, 1.0, GRB.BINARY, name);
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

                /* free the resources */
                model.dispose();
                env.dispose();
            }


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

    private static final BpSeq TEST_BP_SEQ = BpSeq.fromString("1 c 69\n" +
            "2 U 0\n" +
            "3 G 68\n" +
            "4 A 67\n" +
            "5 C 0\n" +
            "6 U 65\n" +
            "7 A 64\n" +
            "8 U 0\n" +
            "9 G 0\n" +
            "10 U 0\n" +
            "11 G 0\n" +
            "12 A 0\n" +
            "13 U 0\n" +
            "14 C 59\n" +
            "15 U 58\n" +
            "16 U 56\n" +
            "17 A 55\n" +
            "18 U 54\n" +
            "19 U 0\n" +
            "20 A 0\n" +
            "21 A 51\n" +
            "22 A 50\n" +
            "23 A 49\n" +
            "24 U 48\n" +
            "25 U 47\n" +
            "26 A 46\n" +
            "27 G 45\n" +
            "28 G 29\n" +
            "29 U 28\n" +
            "30 U 0\n" +
            "31 A 0\n" +
            "32 A 0\n" +
            "33 A 0\n" +
            "34 c 0\n" +
            "35 G 0\n" +
            "36 A 0\n" +
            "37 G 0\n" +
            "38 G 0\n" +
            "39 U 0\n" +
            "40 U 0\n" +
            "41 A 0\n" +
            "42 A 0\n" +
            "43 A 0\n" +
            "44 A 0\n" +
            "45 A 27\n" +
            "46 U 26\n" +
            "47 A 25\n" +
            "48 G 24\n" +
            "49 U 23\n" +
            "50 U 22\n" +
            "51 U 21\n" +
            "52 U 0\n" +
            "53 A 0\n" +
            "54 A 18\n" +
            "55 U 17\n" +
            "56 A 16\n" +
            "57 U 0\n" +
            "58 U 15\n" +
            "59 G 14\n" +
            "60 C 0\n" +
            "61 U 0\n" +
            "62 A 0\n" +
            "63 U 0\n" +
            "64 A 7\n" +
            "65 G 6\n" +
            "66 U 0\n" +
            "67 a 4\n" +
            "68 G 3\n" +
            "69 A 1\n" +
            "70 G 102\n" +
            "71 G 101\n" +
            "72 U 100\n" +
            "73 C 99\n" +
            "74 U 98\n" +
            "75 U 84\n" +
            "76 G 83\n" +
            "77 C 82\n" +
            "78 G 81\n" +
            "79 A 0\n" +
            "80 A 0\n" +
            "81 A 78\n" +
            "82 G 77\n" +
            "83 C 76\n" +
            "84 U 75\n" +
            "85 U 124\n" +
            "86 A 93\n" +
            "87 C 0\n" +
            "88 C 0\n" +
            "89 A 128\n" +
            "90 C 127\n" +
            "91 A 125\n" +
            "92 C 0\n" +
            "93 A 86\n" +
            "94 A 123\n" +
            "95 G 122\n" +
            "96 A 121\n" +
            "97 U 120\n" +
            "98 G 74\n" +
            "99 G 73\n" +
            "100 A 72\n" +
            "101 C 71\n" +
            "102 C 70\n" +
            "103 G 116\n" +
            "104 G 115\n" +
            "105 A 0\n" +
            "106 G 113\n" +
            "107 C 112\n" +
            "108 G 111\n" +
            "109 A 0\n" +
            "110 A 0\n" +
            "111 A 108\n" +
            "112 G 107\n" +
            "113 C 106\n" +
            "114 U 0\n" +
            "115 C 104\n" +
            "116 C 103\n" +
            "117 A 0\n" +
            "118 A 0\n" +
            "119 U 0\n" +
            "120 A 97\n" +
            "121 U 96\n" +
            "122 C 95\n" +
            "123 U 94\n" +
            "124 A 85\n" +
            "125 G 91\n" +
            "126 U 0\n" +
            "127 G 90\n" +
            "128 U 89\n" +
            "129 A 0\n" +
            "130 C 0\n" +
            "131 C 0\n" +
            "132 C 0\n" +
            "133 U 0\n" +
            "134 C 0\n" +
            "135 G 0\n");

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
                while (i < bpseqSize && (entries.get(i).pair() == -1 || (entries.get(i).pair() != -1 && entries.get(i).pair() < i))) {
                    i++;
                }
                if (i >= bpseqSize) break;
                int j = i;
                while ((j < bpseqSize) && (entries.get(j + 1).pair() != -1) && (entries.get(j).pair() == (entries.get(j + 1).pair() + 1)))
                    j++;
                nodes.add(new Node(nodeNum++, i, entries.get(i).pair(), j - i + 1));
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
}
