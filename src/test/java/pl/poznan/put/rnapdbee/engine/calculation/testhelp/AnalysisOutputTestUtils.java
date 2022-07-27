package pl.poznan.put.rnapdbee.engine.calculation.testhelp;

import edu.put.rnapdbee.analysis.AnalysisOutput;
import org.junit.jupiter.api.Assertions;

public class AnalysisOutputTestUtils {

    public static void assertAnalysisOutput(AnalysisOutput analysisOutput,
                                            AnalysisOutputTestInformation expectedInformationAboutAnalysis) {
        Assertions.assertEquals(expectedInformationAboutAnalysis.getBpSeqSize(), analysisOutput.bpSeq().size());
        Assertions.assertEquals(expectedInformationAboutAnalysis.getCtEntriesSize(), analysisOutput.ct().entries().size());
        Assertions.assertEquals(expectedInformationAboutAnalysis.getDotBracketLength(), analysisOutput.dotBracket().length());
        Assertions.assertEquals(expectedInformationAboutAnalysis.getStrandsSize(), analysisOutput.dotBracket().strands().size());
        Assertions.assertEquals(expectedInformationAboutAnalysis.getInteractionsSize(), analysisOutput.getInterStrand().size());
        Assertions.assertEquals(expectedInformationAboutAnalysis.getStructuralElementStemsSize(), analysisOutput.structuralElementFinder().getStems().size());
        Assertions.assertEquals(expectedInformationAboutAnalysis.getStructuralElementSLoopsSize(), analysisOutput.structuralElementFinder().getLoops().size());
        Assertions.assertEquals(expectedInformationAboutAnalysis.getStructuralElementSingleStrandsSize(), analysisOutput.structuralElementFinder().getSingleStrands().size());
        Assertions.assertEquals(expectedInformationAboutAnalysis.getStructuralElementSingleStrands5pSize(), analysisOutput.structuralElementFinder().getSingleStrands5p().size());
        Assertions.assertEquals(expectedInformationAboutAnalysis.getStructuralElementSingleStrands3pSize(), analysisOutput.structuralElementFinder().getSingleStrands3p().size());
    }
}
