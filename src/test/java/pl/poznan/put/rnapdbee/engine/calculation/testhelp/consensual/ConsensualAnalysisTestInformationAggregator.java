package pl.poznan.put.rnapdbee.engine.calculation.testhelp.consensual;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

import java.util.ArrayList;
import java.util.List;

public class ConsensualAnalysisTestInformationAggregator implements ArgumentsAggregator {

    // each entry has only 1 bpseq, 1 ct and 1 dot bracket which are aggregated.
    private static final int NUMBER_OF_AGGREGATED_ARGUMENTS_FOR_EACH_MODEL = 3;

    @Override
    public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext)
            throws ArgumentsAggregationException {
        // assuming there is only 1 aggregated parameter in the test
        final int numberOfNotAggregatedArgumentsInTestMethod = parameterContext.getDeclaringExecutable()
                .getParameters().length - 1;
        List<ConsensualAnalysisTestInformation> testInformationArrayList = new ArrayList<>();
        int modelsInTestCase = (argumentsAccessor.size() - (numberOfNotAggregatedArgumentsInTestMethod - 1))
                / NUMBER_OF_AGGREGATED_ARGUMENTS_FOR_EACH_MODEL;

        for (int modelIndex = 0; modelIndex < modelsInTestCase; ++modelIndex) {
            int indexOfNthBpSeq = numberOfNotAggregatedArgumentsInTestMethod + modelIndex * NUMBER_OF_AGGREGATED_ARGUMENTS_FOR_EACH_MODEL;

            int bpSeqSize = argumentsAccessor.getInteger(indexOfNthBpSeq);
            int ctEntriesSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 1);
            int strandsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 2);

            ConsensualAnalysisTestInformation consensualAnalysisTestInformation = new ConsensualAnalysisTestInformation();
            consensualAnalysisTestInformation.setBpSeqSize(bpSeqSize);
            consensualAnalysisTestInformation.setCtEntriesSize(ctEntriesSize);
            consensualAnalysisTestInformation.setStrandsSize(strandsSize);
            testInformationArrayList.add(consensualAnalysisTestInformation);
        }

        return testInformationArrayList;
    }
}
