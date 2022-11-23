package pl.poznan.put.rnapdbee.engine.testhelp.consensual;

import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMulti;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMultiEntry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsensualAnalysisTestUtils {

    public static void assertAnalysisOutput(OutputMulti analysisOutput,
                                            List<ConsensualAnalysisTestInformation> secondaryAnalysisOutputTestInformationList) {
        assertEquals(secondaryAnalysisOutputTestInformationList.size(), analysisOutput.getEntries().size(),
                "Analysis output length must be correct");
        for (int assertedListItemIndex = 0; assertedListItemIndex < secondaryAnalysisOutputTestInformationList.size(); ++assertedListItemIndex) {
            assertSingleEntryOfAnalysisOutput(analysisOutput.getEntries().get(assertedListItemIndex),
                    secondaryAnalysisOutputTestInformationList.get(assertedListItemIndex));
        }
    }

    private static void assertSingleEntryOfAnalysisOutput(OutputMultiEntry outputMultiEntry,
                                                          ConsensualAnalysisTestInformation expectedInformationAboutAnalysis) {
        var output2DEntry = outputMultiEntry.getOutput2D();
        assertAll("The sizes of analysis's information lists must be the same as expected",
                () -> assertEquals(expectedInformationAboutAnalysis.getBpSeqSize(), output2DEntry.getBpSeq().size(),
                        "bpSeq size is incorrect"),
                () -> assertEquals(expectedInformationAboutAnalysis.getCtEntriesSize(), output2DEntry.getCt().size(),
                        "CT size is incorrect"),
                () -> assertEquals(expectedInformationAboutAnalysis.getStrandsSize(), output2DEntry.getStrands().size(),
                        "Strands size is incorrect")
        );
    }
}
