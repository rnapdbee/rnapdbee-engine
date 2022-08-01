package pl.poznan.put.rnapdbee.engine.calculation.testhelp;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

import java.util.ArrayList;
import java.util.List;

public class AnalysisOutputTestInformationAggregator implements ArgumentsAggregator {

    private static final int NUMBER_OF_AGGREGATED_ARGUMENTS_FOR_EACH_MODEL = 10;

    @Override
    public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext)
            throws ArgumentsAggregationException {
        // assuming there is only 1 aggregated parameter in the test
        final int numberOfNotAggregatedArgumentsInTestMethod = parameterContext.getDeclaringExecutable()
                .getParameters().length - 1;
        List<AnalysisOutputTestInformation> testInformationArrayList = new ArrayList<>();
        int modelsInTestCase = (argumentsAccessor.size() - (numberOfNotAggregatedArgumentsInTestMethod - 1))
                / NUMBER_OF_AGGREGATED_ARGUMENTS_FOR_EACH_MODEL;

        for (int modelIndex = 0; modelIndex < modelsInTestCase; ++modelIndex) {
            int indexOfNthBpSeq = numberOfNotAggregatedArgumentsInTestMethod + modelIndex * NUMBER_OF_AGGREGATED_ARGUMENTS_FOR_EACH_MODEL;

            int bpSeqSize = argumentsAccessor.getInteger(indexOfNthBpSeq);
            int ctEntriesSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 1);
            int interactionsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 2);
            int dotBracketLength = argumentsAccessor.getInteger(indexOfNthBpSeq + 3);
            int strandsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 4);
            int structuralElementStemsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 5);
            int structuralElementSingleStrandsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 6);
            int structuralElementLoopsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 7);
            int structuralElementSingleStrands3pSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 8);
            int structuralElementSingleStrands5pSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 9);

            testInformationArrayList.add(new AnalysisOutputTestInformation()
                    .withBpSeqSize(bpSeqSize)
                    .withCtEntriesSize(ctEntriesSize)
                    .withInteractionsSize(interactionsSize)
                    .withDotBracketLength(dotBracketLength)
                    .withStrandsSize(strandsSize)
                    .withStructuralElementStemsSize(structuralElementStemsSize)
                    .withStructuralElementSingleStrandsSize(structuralElementSingleStrandsSize)
                    .withStructuralElementSLoopsSize(structuralElementLoopsSize)
                    .withStructuralElementSingleStrands3pSize(structuralElementSingleStrands3pSize)
                    .withStructuralElementSingleStrands5pSize(structuralElementSingleStrands5pSize));
        }

        return testInformationArrayList;
    }
}
