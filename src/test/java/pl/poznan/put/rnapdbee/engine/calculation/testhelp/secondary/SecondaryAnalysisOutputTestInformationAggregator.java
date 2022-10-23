package pl.poznan.put.rnapdbee.engine.calculation.testhelp.secondary;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;


public class SecondaryAnalysisOutputTestInformationAggregator implements ArgumentsAggregator {

    @Override
    public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext)
            throws ArgumentsAggregationException {
        // assuming there is only 1 aggregated parameter in the test
        final int numberOfNotAggregatedArgumentsInTestMethod = parameterContext.getDeclaringExecutable()
                .getParameters().length - 1;

        int bpSeqSize = argumentsAccessor.getInteger(numberOfNotAggregatedArgumentsInTestMethod);
        int ctEntriesSize = argumentsAccessor.getInteger(numberOfNotAggregatedArgumentsInTestMethod + 1);
        int interactionsSize = argumentsAccessor.getInteger(numberOfNotAggregatedArgumentsInTestMethod + 2);
        int strandsSize = argumentsAccessor.getInteger(numberOfNotAggregatedArgumentsInTestMethod + 3);
        int structuralElementStemsSize = argumentsAccessor.getInteger(numberOfNotAggregatedArgumentsInTestMethod + 4);
        int structuralElementSingleStrandsSize = argumentsAccessor.getInteger(numberOfNotAggregatedArgumentsInTestMethod + 5);
        int structuralElementLoopsSize = argumentsAccessor.getInteger(numberOfNotAggregatedArgumentsInTestMethod + 6);
        int structuralElementSingleStrands3pSize = argumentsAccessor.getInteger(numberOfNotAggregatedArgumentsInTestMethod + 7);
        int structuralElementSingleStrands5pSize = argumentsAccessor.getInteger(numberOfNotAggregatedArgumentsInTestMethod + 8);

        return new SecondaryAnalysisOutputTestInformation()
                .withBpSeqSize(bpSeqSize)
                .withCtEntriesSize(ctEntriesSize)
                .withInteractionsSize(interactionsSize)
                .withStrandsSize(strandsSize)
                .withStructuralElementStemsSize(structuralElementStemsSize)
                .withStructuralElementSingleStrandsSize(structuralElementSingleStrandsSize)
                .withStructuralElementSLoopsSize(structuralElementLoopsSize)
                .withStructuralElementSingleStrands3pSize(structuralElementSingleStrands3pSize)
                .withStructuralElementSingleStrands5pSize(structuralElementSingleStrands5pSize);
    }
}
