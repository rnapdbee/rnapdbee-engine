package pl.poznan.put.rnapdbee.engine.calculation.control;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.poznan.put.rnapdbee.engine.calculation.CalculationService;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.Output2D;
import pl.poznan.put.rnapdbee.engine.infrastructure.control.CalculationController;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.SingleStrandOutput;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.parser.ContentDispositionParser;

@ExtendWith(MockitoExtension.class)
class CalculationControllerTest {

    @Mock
    CalculationService calculationService;

    @Mock
    ContentDispositionParser contentDispositionParser;

    @Mock
    Logger logger;

    @InjectMocks
    CalculationController cut;

    private final static String mockedFilename = "test.ct";
    private final static String mockedHeader = "header;";
    private final static String mockedContent = "mocked content";

    private final static List<String> MOCKED_BP_SEQ = List.of("1 G 0", "2 u 0");
    private final static List<String> MOCKED_CT = List.of("1 G 0 2 0 1", "2 u 1 3 0 2");
    private final static List<String> MOCKED_INTERACTIONS = List.of("A.A181 - Z.U418", "A.U182 - Z.A417");
    private final static byte[] MOCKED_SVG_FILE_ARRAY = "very funky file".getBytes(StandardCharsets.UTF_8);
    private final static VisualizationTool MOCKED_SUCCESSFUL_DRAWER = VisualizationTool.VARNA;
    private final static VisualizationTool MOCKED_FAILED_DRAWER = VisualizationTool.NONE;

    private final static List<String> MOCKED_STEMS = List.of(
            "6 10 CCCGG ((((( YYYRR 262 266 CCGGG ))))) YYRRR",
            "14 24 GGGUGCAGUCU ((((((((((( RRRYRYRRYYY 247 257 AGACUGCAUUC ))))))))))) RRRYYRYRYYY");
    private final static List<String> MOCKED_LOOPS = List.of(
            "3 4 GU {[ RY 107 108 GG ]> RR 11 14 CAUG <..( YRYR 257 261 CUUAC )...} YYYRY",
            "10 14 GCAUG (<..( RYRYR 257 262 CUUACC )...}) YYYRYY");
    private final static List<String> MOCKED_SINGLE_STRANDS = List.of("4 6 UGC [.( YRY", "10 14 GCAUG (<..( RYRYR");
    private final static List<String> MOCKED_SINGLE_STRANDS_5_P = List.of("2 3 uG .{ YR", "2 6 uGUGC .{[.( YRYRY");
    private final static List<String> MOCKED_SINGLE_STRANDS_3_P = List.of("387 390 UGUG )... YRYR", "418 421 UUUU ]... YYYY");

    private final static SingleStrandOutput MOCKED_STRAND = new SingleStrandOutput.SingleStrandOutputBuilder()
            .withName("A")
            .withSequence("sequence")
            .withStructure("structure")
            .build();


    @Test
    public void shouldPopulateResponseEntityWithTheMappedResponseWhenTheCalculateSecondaryToDotBracketCalculationIsSuccessful() {
        var expectedSingleAnalysis = provideMockedOutput2D();
        // mocked
        Mockito.when(calculationService.handleSecondaryToDotBracketCalculation(Mockito.any(),
                        Mockito.any(), Mockito.eq(true), Mockito.eq(mockedContent), Mockito.eq(mockedFilename)))
                .thenReturn(expectedSingleAnalysis);
        Mockito.when(contentDispositionParser.parseContentDispositionHeader(mockedHeader))
                .thenReturn(mockedFilename);
        // when
        ResponseEntity<Output2D> response = cut
                .calculateSecondaryToDotBracket(null, null, true,
                        mockedHeader, mockedContent);
        // then
        var actualSingleAnalysis = Objects.requireNonNull(response.getBody());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedSingleAnalysis.getBpSeq(), actualSingleAnalysis.getBpSeq());
        Assertions.assertEquals(expectedSingleAnalysis.getCt(), actualSingleAnalysis.getCt());
        Assertions.assertEquals(expectedSingleAnalysis.getInteractions(), actualSingleAnalysis.getInteractions());

        Assertions.assertEquals(expectedSingleAnalysis.getStrands().get(0).getFullName(), actualSingleAnalysis.getStrands().get(0).getFullName());
        Assertions.assertEquals(expectedSingleAnalysis.getStrands().get(0).getSequence(), actualSingleAnalysis.getStrands().get(0).getSequence());
        Assertions.assertEquals(expectedSingleAnalysis.getStrands().get(0).getStructure(), actualSingleAnalysis.getStrands().get(0).getStructure());

        Assertions.assertEquals(expectedSingleAnalysis.getStructuralElements().getSingleStrands(), actualSingleAnalysis.getStructuralElements().getSingleStrands());
        Assertions.assertEquals(expectedSingleAnalysis.getStructuralElements().getSingleStrands5p(), actualSingleAnalysis.getStructuralElements().getSingleStrands5p());
        Assertions.assertEquals(expectedSingleAnalysis.getStructuralElements().getSingleStrands3p(), actualSingleAnalysis.getStructuralElements().getSingleStrands3p());
        Assertions.assertEquals(expectedSingleAnalysis.getStructuralElements().getLoops(), actualSingleAnalysis.getStructuralElements().getLoops());
        Assertions.assertEquals(expectedSingleAnalysis.getStructuralElements().getStems(), actualSingleAnalysis.getStructuralElements().getStems());

        Assertions.assertEquals(expectedSingleAnalysis.getImageInformation().getFailedVisualizationTool(), actualSingleAnalysis.getImageInformation().getFailedVisualizationTool());
        Assertions.assertEquals(expectedSingleAnalysis.getImageInformation().getSuccessfulVisualizationTool(), actualSingleAnalysis.getImageInformation().getSuccessfulVisualizationTool());
        Assertions.assertEquals(expectedSingleAnalysis.getImageInformation().getSvgFile(), actualSingleAnalysis.getImageInformation().getSvgFile());
    }


    private Output2D provideMockedOutput2D() {
        return new Output2D.Output2DBuilder()
                .withBpSeq(MOCKED_BP_SEQ)
                .withCt(MOCKED_CT)
                .withImageInformation(new ImageInformationOutput()
                        .withSvgFile(MOCKED_SVG_FILE_ARRAY)
                        .withSuccessfulDrawer(MOCKED_SUCCESSFUL_DRAWER)
                        .withFailedDrawer(MOCKED_FAILED_DRAWER))
                .withInteractions(MOCKED_INTERACTIONS)
                .withStructuralElement(new StructuralElementOutput.Builder()
                        .withLoops(MOCKED_LOOPS)
                        .withSingleStrands(MOCKED_SINGLE_STRANDS)
                        .withStems(MOCKED_STEMS)
                        .withSingleStrands5p(MOCKED_SINGLE_STRANDS_5_P)
                        .withSingleStrands3p(MOCKED_SINGLE_STRANDS_3_P)
                        .build())
                .withStrands(List.of(MOCKED_STRAND))
                .build();
    }
}
