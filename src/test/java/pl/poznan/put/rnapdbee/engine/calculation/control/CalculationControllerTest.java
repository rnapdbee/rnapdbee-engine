package pl.poznan.put.rnapdbee.engine.calculation.control;

import edu.put.rnapdbee.enums.DrawerEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.poznan.put.rnapdbee.engine.calculation.logic.SecondaryStructureAnalysisService;
import pl.poznan.put.rnapdbee.engine.calculation.logic.EncodingUtils;
import pl.poznan.put.rnapdbee.engine.calculation.mapper.AnalysisOutputsMapper;
import pl.poznan.put.rnapdbee.engine.calculation.model.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.calculation.model.Output2D;
import pl.poznan.put.rnapdbee.engine.calculation.model.SingleSecondaryModelAnalysisOutput;
import pl.poznan.put.rnapdbee.engine.calculation.model.SingleStrandOutput;
import pl.poznan.put.rnapdbee.engine.calculation.model.StructuralElementOutput;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


@SpringBootTest
class CalculationControllerTest {

    @MockBean
    SecondaryStructureAnalysisService secondaryStructureAnalysisService;

    @MockBean
    AnalysisOutputsMapper analysisOutputsMapper;

    @Autowired
    CalculationController cut;

    private final static String mockedFilename = "test.cif";
    private final static String mockedContent = "mocked content";

    private final static List<String> MOCKED_BP_SEQ = List.of("1 G 0", "2 u 0");
    private final static List<String> MOCKED_CT = List.of("1 G 0 2 0 1", "2 u 1 3 0 2");
    private final static List<String> MOCKED_INTERACTIONS = List.of("A.A181 - Z.U418", "A.U182 - Z.A417");
    private final static String MOCKED_PATH_TO_PNG = "path/to/png";
    private final static String MOCKED_PATH_TO_SVG = "path/to/svg";
    private final static DrawerEnum MOCKED_SUCCESSFUL_DRAWER = DrawerEnum.VARNA;
    private final static DrawerEnum MOCKED_FAILED_DRAWER = DrawerEnum.NONE;

    private final static List<String> MOCKED_STEMS = List.of(
            "6 10 CCCGG ((((( YYYRR 262 266 CCGGG ))))) YYRRR",
            "14 24 GGGUGCAGUCU ((((((((((( RRRYRYRRYYY 247 257 AGACUGCAUUC ))))))))))) RRRYYRYRYYY");
    private final static List<String> MOCKED_LOOPS = List.of(
            "3 4 GU {[ RY 107 108 GG ]> RR 11 14 CAUG <..( YRYR 257 261 CUUAC )...} YYYRY",
            "10 14 GCAUG (<..( RYRYR 257 262 CUUACC )...}) YYYRYY");
    private final static List<String> MOCKED_SINGLE_STRANDS = List.of("4 6 UGC [.( YRY", "10 14 GCAUG (<..( RYRYR");
    private final static List<String> MOCKED_SINGLE_STRANDS_5_P = List.of("2 3 uG .{ YR", "2 6 uGUGC .{[.( YRYRY");
    private final static List<String> MOCKED_SINGLE_STRANDS_3_P = List.of("387 390 UGUG )... YRYR", "418 421 UUUU ]... YYYY");

    private final static SingleStrandOutput MOCKED_STRAND = new SingleStrandOutput()
            .withName("A")
            .withSequence("sequence")
            .withStructure("structure");


    @Test
    public void shouldPopulateResponseEntityWithTheMappedResponseWhenTheCalculateDBToImageCalculationIsSuccessful() {
        // creating mocked scope in which static methods are mocked ones
        try (MockedStatic<EncodingUtils> mockedEncodingUtils = Mockito.mockStatic(EncodingUtils.class)) {
            var analysisOutput = provideMockedOutput2D();
            var expectedSingleAnalysis = analysisOutput.getAnalysis().get(0);
            // mocked
            mockedEncodingUtils.when(() -> EncodingUtils.decodeBase64ToString(Mockito.any())).
                    thenReturn(mockedContent);
            Mockito.when(secondaryStructureAnalysisService.analyseDotBracketNotationFile(Mockito.any(),
                            Mockito.any(), Mockito.eq(mockedContent), Mockito.eq(mockedFilename)))
                    .thenReturn(Collections.emptyList());
            Mockito.when(analysisOutputsMapper.mapToOutput2D(Mockito.any()))
                    .thenReturn(analysisOutput);

            // when
            ResponseEntity<Output2D> response = cut
                    .calculateDotBracketToImage(null, null,
                            "Attachment; filename=\"" + mockedFilename + "\"", mockedContent);
            // then
            var actualSingleAnalysis = Objects.requireNonNull(response.getBody()).getAnalysis().get(0);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(expectedSingleAnalysis.getBpSeq(), actualSingleAnalysis.getBpSeq());
            Assertions.assertEquals(expectedSingleAnalysis.getCt(), actualSingleAnalysis.getCt());
            Assertions.assertEquals(expectedSingleAnalysis.getInteractions(), actualSingleAnalysis.getInteractions());

            Assertions.assertEquals(expectedSingleAnalysis.getStrands().get(0).getName(), actualSingleAnalysis.getStrands().get(0).getName());
            Assertions.assertEquals(expectedSingleAnalysis.getStrands().get(0).getSequence(), actualSingleAnalysis.getStrands().get(0).getSequence());
            Assertions.assertEquals(expectedSingleAnalysis.getStrands().get(0).getStructure(), actualSingleAnalysis.getStrands().get(0).getStructure());

            Assertions.assertEquals(expectedSingleAnalysis.getStructuralElements().getSingleStrands(), actualSingleAnalysis.getStructuralElements().getSingleStrands());
            Assertions.assertEquals(expectedSingleAnalysis.getStructuralElements().getSingleStrands5p(), actualSingleAnalysis.getStructuralElements().getSingleStrands5p());
            Assertions.assertEquals(expectedSingleAnalysis.getStructuralElements().getSingleStrands3p(), actualSingleAnalysis.getStructuralElements().getSingleStrands3p());
            Assertions.assertEquals(expectedSingleAnalysis.getStructuralElements().getLoops(), actualSingleAnalysis.getStructuralElements().getLoops());
            Assertions.assertEquals(expectedSingleAnalysis.getStructuralElements().getStems(), actualSingleAnalysis.getStructuralElements().getStems());

            Assertions.assertEquals(expectedSingleAnalysis.getImageInformation().getFailedDrawer(), actualSingleAnalysis.getImageInformation().getFailedDrawer());
            Assertions.assertEquals(expectedSingleAnalysis.getImageInformation().getSuccessfulDrawer(), actualSingleAnalysis.getImageInformation().getSuccessfulDrawer());
            Assertions.assertEquals(expectedSingleAnalysis.getImageInformation().getPathToPNGImage(), actualSingleAnalysis.getImageInformation().getPathToPNGImage());
            Assertions.assertEquals(expectedSingleAnalysis.getImageInformation().getPathToSVGImage(), actualSingleAnalysis.getImageInformation().getPathToSVGImage());
        }
    }


    private Output2D provideMockedOutput2D() {
        var mockedAnalysis = Collections.singletonList(new SingleSecondaryModelAnalysisOutput()
                .withBpSeq(MOCKED_BP_SEQ)
                .withCt(MOCKED_CT)
                .withImageInformation(new ImageInformationOutput()
                        .withPathToPNGImage(MOCKED_PATH_TO_PNG)
                        .withPathToSVGImage(MOCKED_PATH_TO_SVG)
                        .withSuccessfulDrawer(MOCKED_SUCCESSFUL_DRAWER)
                        .withFailedDrawer(MOCKED_FAILED_DRAWER))
                .withInteractions(MOCKED_INTERACTIONS)
                .withStructuralElement(new StructuralElementOutput()
                        .withLoops(MOCKED_LOOPS)
                        .withSingleStrands(MOCKED_SINGLE_STRANDS)
                        .withStems(MOCKED_STEMS)
                        .withSingleStrands5p(MOCKED_SINGLE_STRANDS_5_P)
                        .withSingleStrands3p(MOCKED_SINGLE_STRANDS_3_P))
                .withStrands(List.of(MOCKED_STRAND))
        );
        return new Output2D()
                .withAnalysis(mockedAnalysis);
    }
}
