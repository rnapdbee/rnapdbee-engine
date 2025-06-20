package pl.poznan.put.rnapdbee.engine.testhelp.tertiary;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

import java.util.ArrayList;
import java.util.List;

public class TertiaryAnalysisOutputTestInformationAggregator implements ArgumentsAggregator {

    private static final int NUMBER_OF_AGGREGATED_ARGUMENTS_FOR_EACH_MODEL = 16;

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
            int strandsEntriesSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 2);
            int structuralElementStemsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 3);
            int structuralElementSLoopsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 4);
            int structuralElementSingleStrandsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 5);
            int structuralElementSingleStrands5pSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 6);
            int structuralElementSingleStrands3pSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 7);
            int coordinatesLineLength = argumentsAccessor.getInteger(indexOfNthBpSeq + 8);
            int messagesSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 9);
            int canonicalInteractionsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 10);
            int nonCanonicalInteractionsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 11);
            int interStrandInteractionsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 12);
            int stackingInteractionsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 13);
            int basePhosphateInteractionsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 14);
            int baseRiboseInteractionsSize = argumentsAccessor.getInteger(indexOfNthBpSeq + 15);

            var tertiaryAnalysisOutputTestInformation = new TertiaryAnalysisOutputTestInformation.Builder()
                    .withBpSeqSize(bpSeqSize)
                    .withCtSize(ctEntriesSize)
                    .withStrandsSize(strandsEntriesSize)
                    .withStructuralElementStemsSize(structuralElementStemsSize)
                    .withStructuralElementSLoopsSize(structuralElementSLoopsSize)
                    .withStructuralElementSingleStrandsSize(structuralElementSingleStrandsSize)
                    .withStructuralElementSingleStrands5pSize(structuralElementSingleStrands5pSize)
                    .withStructuralElementSingleStrands3pSize(structuralElementSingleStrands3pSize)
                    .withCoordinatesLineLength(coordinatesLineLength)
                    .withMessagesSize(messagesSize)
                    .withCanonicalInteractionsSize(canonicalInteractionsSize)
                    .withNonCanonicalInteractionsSize(nonCanonicalInteractionsSize)
                    .withInterStrandInteractionsSize(interStrandInteractionsSize)
                    .withStackingInteractionsSize(stackingInteractionsSize)
                    .withBasePhosphateInteractionsSize(basePhosphateInteractionsSize)
                    .withBaseRiboseInteractionsSize(baseRiboseInteractionsSize)
                    .build();
            testInformationArrayList.add(tertiaryAnalysisOutputTestInformation);
        }
        return testInformationArrayList;
    }
}