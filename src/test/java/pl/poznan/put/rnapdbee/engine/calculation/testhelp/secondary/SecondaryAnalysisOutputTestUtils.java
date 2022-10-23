package pl.poznan.put.rnapdbee.engine.calculation.testhelp.secondary;

import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.Output2D;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SecondaryAnalysisOutputTestUtils {

    public static void assertAnalysisOutputs(Output2D analysisOutput,
                                             SecondaryAnalysisOutputTestInformation secondaryAnalysisOutputTestInformation) {
        assertAll("The sizes of analysis's information lists must be the same as expected",
                () -> assertEquals(secondaryAnalysisOutputTestInformation.getBpSeqSize(), analysisOutput.getBpSeq().size(),
                        "bpSeq size is incorrect"),
                () -> assertEquals(secondaryAnalysisOutputTestInformation.getCtEntriesSize(), analysisOutput.getCt().size(),
                        "CT size is incorrect"),
                () -> assertEquals(secondaryAnalysisOutputTestInformation.getInteractionsSize(), analysisOutput.getInteractions().size(),
                        "Interactions size is incorrect"),
                () -> assertEquals(secondaryAnalysisOutputTestInformation.getStrandsSize(), analysisOutput.getStrands().size(),
                        "Strands size is incorrect"),
                () -> assertEquals(secondaryAnalysisOutputTestInformation.getStructuralElementStemsSize(),
                        analysisOutput.getStructuralElements().getStems().size(),
                        "Structural Element's Stems size is incorrect"),
                () -> assertEquals(secondaryAnalysisOutputTestInformation.getStructuralElementSLoopsSize(),
                        analysisOutput.getStructuralElements().getLoops().size(),
                        "Structural Element's Loops size is incorrect"),
                () -> assertEquals(secondaryAnalysisOutputTestInformation.getStructuralElementSingleStrandsSize(),
                        analysisOutput.getStructuralElements().getSingleStrands().size(),
                        "Structural Element's Single Strands size is incorrect"),
                () -> assertEquals(secondaryAnalysisOutputTestInformation.getStructuralElementSingleStrands5pSize(),
                        analysisOutput.getStructuralElements().getSingleStrands5p().size(),
                        "Structural Element's Single Strands 5p size is incorrect"),
                () -> assertEquals(secondaryAnalysisOutputTestInformation.getStructuralElementSingleStrands3pSize(),
                        analysisOutput.getStructuralElements().getSingleStrands3p().size(),
                        "Structural Element's Single Strands 3p size is incorrect")
        );
    }
}
