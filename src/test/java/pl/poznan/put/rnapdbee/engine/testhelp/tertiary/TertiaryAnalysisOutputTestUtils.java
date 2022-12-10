package pl.poznan.put.rnapdbee.engine.testhelp.tertiary;

import pl.poznan.put.rnapdbee.engine.calculation.tertiary.domain.Output3D;
import pl.poznan.put.rnapdbee.engine.calculation.tertiary.domain.SingleTertiaryModelOutput;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TertiaryAnalysisOutputTestUtils {

    public static void assertAnalysisOutputs(Output3D tertiaryAnalysisOutput,
                                             List<TertiaryAnalysisOutputTestInformation> expectedInformation) {
        assertEquals(tertiaryAnalysisOutput.getModels().size(), expectedInformation.size(),
                "Analysis output length must be correct");
        for (int assertedListItemIndex = 0; assertedListItemIndex < expectedInformation.size(); ++assertedListItemIndex) {
            assertAnalysisOutput(tertiaryAnalysisOutput.getModels().get(assertedListItemIndex),
                    expectedInformation.get(assertedListItemIndex));
            assertEquals(assertedListItemIndex + 1, tertiaryAnalysisOutput.getModels().get(assertedListItemIndex).getModelNumber());
        }
    }

    private static void assertAnalysisOutput(SingleTertiaryModelOutput singleTertiaryModelOutput,
                                             TertiaryAnalysisOutputTestInformation expectedInformation) {
        assertAll("The sizes of analysis's information lists must be the same as expected",
                () -> assertEquals(expectedInformation.getBpSeqSize(), singleTertiaryModelOutput.getOutput2D()
                                .getBpSeq().size(),
                        "bpSeq size is incorrect"),
                () -> assertEquals(expectedInformation.getCtSize(), singleTertiaryModelOutput.getOutput2D()
                                .getCt().size(),
                        "CT size is incorrect"),
                () -> assertEquals(expectedInformation.getStrandsSize(), singleTertiaryModelOutput.getOutput2D()
                                .getStrands().size(),
                        "strands size is incorrect"),
                () -> assertEquals(expectedInformation.getStructuralElementStemsSize(), singleTertiaryModelOutput
                                .getOutput2D().getStructuralElements().getStems().size(),
                        "Structural Element's Stems size is incorrect"),
                () -> assertEquals(expectedInformation.getStructuralElementSLoopsSize(), singleTertiaryModelOutput
                                .getOutput2D().getStructuralElements().getLoops().size(),
                        "Structural Element's Loops size is incorrect"),
                () -> assertEquals(expectedInformation.getStructuralElementSingleStrandsSize(), singleTertiaryModelOutput
                                .getOutput2D().getStructuralElements().getSingleStrands().size(),
                        "Structural Element's Single Strands size is incorrect"),
                () -> assertEquals(expectedInformation.getStructuralElementSingleStrands5pSize(), singleTertiaryModelOutput
                                .getOutput2D().getStructuralElements().getSingleStrands5p().size(),
                        "Structural Element's Single Strands 5p size is incorrect"),
                () -> assertEquals(expectedInformation.getStructuralElementSingleStrands3pSize(), singleTertiaryModelOutput
                                .getOutput2D().getStructuralElements().getSingleStrands3p().size(),
                        "Structural Element's Single Strands 3p size is incorrect"),
                () -> assertEquals(expectedInformation.getCoordinatesLineLength(), singleTertiaryModelOutput
                                .getOutput2D().getStructuralElements().getCoordinates().length(),
                        "Length of coordinates (output pdb file) is incorrect"),
                () -> assertEquals(expectedInformation.getMessagesSize(), singleTertiaryModelOutput
                                .getMessages().size(),
                        "Messages size is incorrect"),
                () -> assertEquals(expectedInformation.getCanonicalInteractionsSize(), singleTertiaryModelOutput
                                .getCanonicalInteractions().size(),
                        "Canonical interactions size is incorrect"),
                () -> assertEquals(expectedInformation.getNonCanonicalInteractionsSize(), singleTertiaryModelOutput
                                .getNonCanonicalInteractions().size(),
                        "Non-canonical interactions size is incorrect"),
                () -> assertEquals(expectedInformation.getInterStrandInteractionsSize(), singleTertiaryModelOutput
                                .getInterStrandInteractions().size(),
                        "Inter strand interactions size is incorrect"),
                () -> assertEquals(expectedInformation.getStackingInteractionsSize(), singleTertiaryModelOutput
                                .getStackingInteractions().size(),
                        "Stacking interactions size is incorrect"),
                () -> assertEquals(expectedInformation.getBasePhosphateInteractionsSize(), singleTertiaryModelOutput
                                .getBasePhosphateInteractions().size(),
                        "Base-Phosphate interactions size is incorrect"),
                () -> assertEquals(expectedInformation.getBaseRiboseInteractionsSize(), singleTertiaryModelOutput
                                .getBaseRiboseInteractions().size(),
                        "Base-Ribose interactions size is incorrect")
        );
    }
}
