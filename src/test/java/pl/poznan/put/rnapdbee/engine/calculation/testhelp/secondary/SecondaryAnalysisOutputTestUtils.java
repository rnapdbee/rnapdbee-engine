package pl.poznan.put.rnapdbee.engine.calculation.testhelp.secondary;

import edu.put.rnapdbee.analysis.AnalysisOutput;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

public class SecondaryAnalysisOutputTestUtils {

    public static void assertAnalysisOutputs(List<AnalysisOutput> analysisOutputList,
                                             List<SecondaryAnalysisOutputTestInformation> secondaryAnalysisOutputTestInformationList) {
        assertEquals(secondaryAnalysisOutputTestInformationList.size(), analysisOutputList.size(),
                "Analysis output length must be correct");
        for (int assertedListItemIndex = 0; assertedListItemIndex < secondaryAnalysisOutputTestInformationList.size(); ++assertedListItemIndex) {
            assertAnalysisOutput(analysisOutputList.get(assertedListItemIndex),
                    secondaryAnalysisOutputTestInformationList.get(assertedListItemIndex));
        }
    }

    private static void assertAnalysisOutput(AnalysisOutput analysisOutput,
                                             SecondaryAnalysisOutputTestInformation expectedInformationAboutAnalysis) {
        assertAll("The sizes of analysis's information lists must be the same as expected",
                () -> assertEquals(expectedInformationAboutAnalysis.getBpSeqSize(), analysisOutput.bpSeq().size(),
                        "bpSeq size is incorrect"),
                () -> assertEquals(expectedInformationAboutAnalysis.getCtEntriesSize(), analysisOutput.ct().entries().size(),
                        "CT size is incorrect"),
                () -> assertEquals(expectedInformationAboutAnalysis.getInteractionsSize(), analysisOutput.getInterStrand().size(),
                        "Interactions size is incorrect"),
                () -> assertEquals(expectedInformationAboutAnalysis.getDotBracketLength(), analysisOutput.dotBracket().length(),
                        "dotBracket size is incorrect"),
                () -> assertEquals(expectedInformationAboutAnalysis.getStrandsSize(), analysisOutput.dotBracket().strands().size(),
                        "Strands size is incorrect"),
                () -> assertEquals(expectedInformationAboutAnalysis.getStructuralElementStemsSize(),
                        analysisOutput.structuralElementFinder().getStems().size(),
                        "Structural Element's Stems size is incorrect"),
                () -> assertEquals(expectedInformationAboutAnalysis.getStructuralElementSLoopsSize(),
                        analysisOutput.structuralElementFinder().getLoops().size(),
                        "Structural Element's Loops size is incorrect"),
                () -> assertEquals(expectedInformationAboutAnalysis.getStructuralElementSingleStrandsSize(),
                        analysisOutput.structuralElementFinder().getSingleStrands().size(),
                        "Structural Element's Single Strands size is incorrect"),
                () -> assertEquals(expectedInformationAboutAnalysis.getStructuralElementSingleStrands5pSize(),
                        analysisOutput.structuralElementFinder().getSingleStrands5p().size(),
                        "Structural Element's Single Strands 5p size is incorrect"),
                () -> assertEquals(expectedInformationAboutAnalysis.getStructuralElementSingleStrands3pSize(),
                        analysisOutput.structuralElementFinder().getSingleStrands3p().size(),
                        "Structural Element's Single Strands 3p size is incorrect")
        );
    }
}
