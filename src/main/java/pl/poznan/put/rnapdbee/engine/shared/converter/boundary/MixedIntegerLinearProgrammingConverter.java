package pl.poznan.put.rnapdbee.engine.shared.converter.boundary;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.engine.shared.converter.logic.BracketTranslation;
import pl.poznan.put.rnapdbee.engine.shared.converter.domain.IntervalGraph;
import pl.poznan.put.rnapdbee.engine.shared.converter.domain.Node;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.DefaultDotBracket;
import pl.poznan.put.structure.formats.DotBracket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class implementing converting BpSeq structure into DotBracket structure using Mixed Integer Linear Programming approach.
 * The implementation is written with Gurobi optimizer.
 */
@Service
public class MixedIntegerLinearProgrammingConverter implements Converter {

    private final Logger logger;

    private static final String GUROBI_ERROR_MET_FORMAT = "Gurobi error faced during the conversion with code: %s and message: %s";
    private static final int[] WEIGTHS = {10, -1, -2, -3, -4, -5, -6, -7, -8, -9};
    private static final int MAX_BRACKET = 10;

    @Override
    public DotBracket convert(BpSeq bpSeq) {
        try {
            IntervalGraph intervalGraph = new IntervalGraph(bpSeq);
            List<Integer> component = new ArrayList<>();
            int[] position = new int[intervalGraph.getNodes().size()];
            HashSet<Integer> visited = new HashSet<>();

            for (int nodeIndex = 0; nodeIndex < intervalGraph.getNodes().size(); ++nodeIndex) {

                if (visited.contains(nodeIndex)) {
                    continue;
                }

                if (intervalGraph.nodeHasNoEdges(nodeIndex)) {
                    visited.add(nodeIndex);
                    intervalGraph.getNodes().get(nodeIndex).setBracketing(0);
                    continue;
                }

                depthFirstSearch(nodeIndex, intervalGraph, visited, component);

                for (int componentIndex = 0; componentIndex < component.size(); componentIndex++) {
                    position[component.get(componentIndex)] = componentIndex;
                }

                GRBEnv env = new GRBEnv(true);
                env.set(GRB.IntParam.LogToConsole, 0);
                env.set(GRB.IntParam.OutputFlag, 0);
                env.start();
                GRBModel model = new GRBModel(env);

                List<GRBVar> variables = createDecisionVariables(component, model);
                GRBLinExpr expr = createObjectiveFunction(intervalGraph, component, variables);
                model.setObjective(expr, GRB.MAXIMIZE);

                createEveryRegionOrderAssignedOnceConstraint(component, model, variables);
                createInterlacingRegionsAreAssignedDifferentPSOrderValuesConstraint(intervalGraph,
                        component, position, model, variables);

                model.optimize();

                setBracketingResultInGraph(intervalGraph, component, variables);

                /* free the resources */
                model.dispose();
                env.dispose();
            }

            return createDotBracket(bpSeq, intervalGraph);

        } catch (GRBException e) {
            logger.error(String.format(GUROBI_ERROR_MET_FORMAT, e.getErrorCode(), e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    private List<GRBVar> createDecisionVariables(List<Integer> component, GRBModel model) {
        return component.stream()
                .flatMap(integer -> IntStream.range(0, MAX_BRACKET)
                        .mapToObj(bracketIndex -> {
                            String name = String.format("n_%s_t_%s", integer, bracketIndex);
                            try {
                                return model.addVar(0.0, 1.0, 0.0, GRB.BINARY, name);
                            } catch (GRBException e) {
                                logger.error(String.format(GUROBI_ERROR_MET_FORMAT, e.getErrorCode(), e.getMessage()));
                                throw new RuntimeException(e);
                            }
                        }))
                .collect(Collectors.toList());
    }

    private GRBLinExpr createObjectiveFunction(IntervalGraph intervalGraph, List<Integer> component, List<GRBVar> variables) {
        GRBLinExpr expr = new GRBLinExpr();
        for (int i = 0; i < component.size(); ++i) {
            for (int j = 0; j < MAX_BRACKET; ++j) {
                GRBVar correspondingVariable = variables.get(i * MAX_BRACKET + j);
                expr.addTerm(
                        WEIGTHS[j] * intervalGraph.getNodes().get(component.get(i)).getWeight(),
                        correspondingVariable);
            }
        }
        return expr;
    }

    private void createEveryRegionOrderAssignedOnceConstraint(List<Integer> component,
                                                              GRBModel model,
                                                              List<GRBVar> variables) throws GRBException {
        for (int i = 0; i < component.size(); ++i) {
            GRBLinExpr constraintExpression = new GRBLinExpr();
            String name = String.format("c_arc_type_%s", i);
            for (int j = 0; j < MAX_BRACKET; ++j) {
                GRBVar correspondingVariable = variables.get(i * MAX_BRACKET + j);
                constraintExpression.addTerm(1.0, correspondingVariable);
            }
            model.addConstr(constraintExpression, GRB.EQUAL, 1.0, name);
        }
    }

    private void createInterlacingRegionsAreAssignedDifferentPSOrderValuesConstraint(
            IntervalGraph intervalGraph,
            List<Integer> component,
            int[] position,
            GRBModel model,
            List<GRBVar> variables) throws GRBException {
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
    }

    private DotBracket createDotBracket(BpSeq bpSeq, IntervalGraph intervalGraph) {
        char[] bracketing = new char[bpSeq.size()];
        Arrays.fill(bracketing, '.');
        for (Node node : intervalGraph.getNodes()) {
            for (int i = 0; i < node.getWeight(); i++) {
                bracketing[node.getStart() + i] = BracketTranslation.getStartingBracket(node.getBracketing());
                bracketing[node.getEnd() - i] = BracketTranslation.getEndingBracket(node.getBracketing());
                if (node.isStartRightAfterEnd()) {
                    break;
                }
            }
        }

        StringBuilder dotBracketBuilder = new StringBuilder();
        dotBracketBuilder.append(">strand\n");
        bpSeq.entries().forEach(entry -> dotBracketBuilder.append(entry.seq()));
        dotBracketBuilder.append("\n");
        dotBracketBuilder.append(bracketing);
        dotBracketBuilder.append("\n");

        return DefaultDotBracket.fromString(dotBracketBuilder.toString());
    }

    private static void depthFirstSearch(int edgeIndex,
                                         IntervalGraph intervalGraph,
                                         HashSet<Integer> visited,
                                         List<Integer> component) {
        visited.add(edgeIndex);
        component.add(edgeIndex);
        intervalGraph.getEdges().get(edgeIndex).forEach(edge -> {
            if (!visited.contains(edge)) {
                depthFirstSearch(edge, intervalGraph, visited, component);
            }
        });
    }

    private void setBracketingResultInGraph(IntervalGraph intervalGraph,
                                            List<Integer> component,
                                            List<GRBVar> variables) throws GRBException {
        for (int i = 0; i < component.size(); ++i) {
            for (int j = 0; j < MAX_BRACKET; ++j) {
                if (variables.get(i * MAX_BRACKET + j).get(GRB.DoubleAttr.X) != 0.0) {
                    intervalGraph.getNodes().get(component.get(i)).setBracketing(j);
                }
            }
        }
    }

    @Autowired
    public MixedIntegerLinearProgrammingConverter(Logger logger) {
        this.logger = logger;
    }
}
