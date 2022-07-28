package pl.poznan.put.rnapdbee.engine.calculation.logic;

import edu.put.rnapdbee.visualization.SecondaryStructureImage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.AnalysisOutputTestInformation;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.AnalysisOutputTestUtils;
import pl.poznan.put.rnapdbee.engine.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.model.StructuralElementsHandling;

import java.util.stream.Stream;


@SpringBootTest
class DotBracketToImageServiceTest {

    @MockBean
    ImageService imageService;

    @Autowired
    DotBracketToImageService cut;

    @BeforeEach
    void provideMocks() {
        SecondaryStructureImage imageMock = Mockito.mock(SecondaryStructureImage.class);
        Mockito.when(imageService.provideVisualization(Mockito.any(), Mockito.any())).thenReturn(imageMock);
    }


    @ParameterizedTest
    @MethodSource("provideTestCases")
    public void testDotBracketToImageService(StructuralElementsHandling structuralElementsHandling,
                                             VisualizationTool visualizationTool,
                                             String content,
                                             AnalysisOutputTestInformation expectedInformation) {
        var actual = cut.performDotBracketToImageCalculation(structuralElementsHandling,
                visualizationTool,
                content,
                "test.pdb");
        AnalysisOutputTestUtils.assertAnalysisOutput(actual.get(0), expectedInformation);
    }


    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(
                        StructuralElementsHandling.USE_PSEUDOKNOTS,
                        VisualizationTool.VARNA,
                        ">strand_A\n" +
                                "GuGUGCCCGGCAUGGGUGCAGUCUAUAGGGUGAGAGUCCCGAACUGUGAAGGCAGAAGUAACAGUUAGCCUAACGCAAGGGUG" +
                                "UCCGUGGCGACAUGGAAUCUGAAGGAAGCGGACGGCAAACCUUCGGUCUGAGGAACACGAACUUCAUAUGAGGCUAGGUAUCA" +
                                "AUGGAUGAGUUUGCAUAACAAAACAAAGUCCUUUCUGCCAAAGUUGGUACAGAGUAAAUGAAGCAGAUUGAUGAAGGGAAAGA" +
                                "CUGCAUUCUUACCCGGGGAGGUCUGGAAACAGAAGUCAGCAGAAGUCAUAGUACCCUGUUCGCAGGGGAAGGACGGAACAAGU" +
                                "AUGGCGUUCGCGCCUAAGCUUGAACCGCCGUAUACCGAACGGUACGUACGGUGGUGUGAGAGGAGUUCGCUCUACUCUAU\n" +
                                "-.{[.(((((<..(((((((((((...(((.......)))..(((((...{{{.{{{...)))))..(((...(((..((((" +
                                ".((((((....))))))))))...]>..)))...)))...(((((((((((.(.....)...(((((.....[((...((((" +
                                ".(((((((..((((.][[[[[.))))...)))).}}}.}}}...))).))))...))...))))))))))...))))))..." +
                                ")))))))))))...})))))(...((((....))))...).......(((.(....(((........)))...))))....(" +
                                "((((..((((....))))...))))).(((((((((((((....)))..))))))))))...--------------------" +
                                "--\n" +
                                ">strand_Z\n" +
                                "uGUUAUUUU\n" +
                                ".]]]]]...\n",
                        new AnalysisOutputTestInformation()
                                .withBpSeqSize(421)
                                .withCtEntriesSize(421)
                                .withInteractionsSize(5)
                                .withDotBracketLength(421)
                                .withStrandsSize(2)
                                .withStructuralElementStemsSize(26)
                                .withStructuralElementSingleStrandsSize(49)
                                .withStructuralElementSLoopsSize(29)
                                .withStructuralElementSingleStrands3pSize(2)
                                .withStructuralElementSingleStrands5pSize(3)
                )
        );
    }
}
