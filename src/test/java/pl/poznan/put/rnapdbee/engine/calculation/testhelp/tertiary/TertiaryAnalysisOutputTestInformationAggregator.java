package pl.poznan.put.rnapdbee.engine.calculation.testhelp.tertiary;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

import java.util.ArrayList;
import java.util.List;

public class TertiaryAnalysisOutputTestInformationAggregator implements ArgumentsAggregator {

    // each entry has only 1 bpseq, 1 ct and 1 dot bracket which are aggregated.
    private static final int NUMBER_OF_AGGREGATED_ARGUMENTS_FOR_EACH_MODEL = 14;

    @Override
    public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext)
            throws ArgumentsAggregationException {
        // assuming there is only 1 aggregated parameter in the test
        final int numberOfNotAggregatedArgumentsInTestMethod = parameterContext.getDeclaringExecutable()
                .getParameters().length - 1;
        List<TertiaryAnalysisOutputTestInformation> testInformationArrayList = new ArrayList<>();
        int modelsInTestCase = (argumentsAccessor.size() - (numberOfNotAggregatedArgumentsInTestMethod - 1))
                / NUMBER_OF_AGGREGATED_ARGUMENTS_FOR_EACH_MODEL;

        for (int modelIndex = 0; modelIndex < modelsInTestCase; ++modelIndex) {
            int indexOfNthBpSeq = numberOfNotAggregatedArgumentsInTestMethod + modelIndex * NUMBER_OF_AGGREGATED_ARGUMENTS_FOR_EACH_MODEL;

            int bpSeqSize = argumentsAccessor.getInteger(indexOfNthBpSeq);
            int ctEntriesSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 1);
            int structuralElementStemsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 2);
            int structuralElementSLoopsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 3);
            int structuralElementSingleStrandsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 4);
            int structuralElementSingleStrands5pSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 5);
            int structuralElementSingleStrands3pSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 6);
            int coordinatesLineLength = argumentsAccessor.getInteger(indexOfNthBpSeq + 7);
            int messagesSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 8);
            int canonicalInteractionsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 9);
            int nonCanonicalInteractionsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 10);
            int stackingInteractionsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 11);
            int basePhosphateInteractionsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 12);
            int baseRiboseInteractionsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 13);

            var tertiaryAnalysisOutputTestInformation = new TertiaryAnalysisOutputTestInformation()
                    .withBpSeqSize(bpSeqSize)
                    .withCtSize(ctEntriesSize)
                    .withStructuralElementStemsSize(structuralElementStemsSize)
                    .withStructuralElementSLoopsSize(structuralElementSLoopsSize)
                    .withStructuralElementSingleStrandsSize(structuralElementSingleStrandsSize)
                    .withStructuralElementSingleStrands5pSize(structuralElementSingleStrands5pSize)
                    .withStructuralElementSingleStrands3pSize(structuralElementSingleStrands3pSize)
                    .withCoordinatesLineLength(coordinatesLineLength)
                    .withMessagesSize(messagesSize)
                    .withCanonicalInteractionsSize(canonicalInteractionsSize)
                    .withNonCanonicalInteractionsSize(nonCanonicalInteractionsSize)
                    .withStackingInteractionsSize(stackingInteractionsSize)
                    .withBasePhosphateInteractionsSize(basePhosphateInteractionsSize)
                    .withBaseRiboseInteractionsSize(baseRiboseInteractionsSize);
            testInformationArrayList.add(tertiaryAnalysisOutputTestInformation);
        }
        return testInformationArrayList;
    }
}