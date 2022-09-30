package pl.poznan.put.rnapdbee.engine.calculation.logic;

import edu.put.rnapdbee.visualization.SecondaryStructureImage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.AnalysisOutputTestInformation;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.AnalysisOutputTestInformationAggregator;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.AnalysisOutputTestUtils;
import pl.poznan.put.rnapdbee.engine.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.model.StructuralElementsHandling;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@SpringBootTest
class SecondaryStructureAnalysisServiceTest {

    static String EXAMPLE_FILE_PATH_FORMAT = "/secondaryTestFiles/%s";

    @MockBean
    ImageService imageService;

    @Autowired
    SecondaryStructureAnalysisService cut;

    @BeforeEach
    void provideMocks() {
        SecondaryStructureImage imageMock = Mockito.mock(SecondaryStructureImage.class);
        Mockito.when(imageService.provideVisualization(Mockito.any(), Mockito.any())).thenReturn(imageMock);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/secondaryToDotBracketTestCases.csv", maxCharsPerColumn = 16384)
    public void testSecondaryStructureFileAnalysis(StructuralElementsHandling structuralElementsHandling,
                                                   VisualizationTool visualizationTool,
                                                   String filename,
                                                   boolean shouldRemoveIsolated,
                                                   @AggregateWith(AnalysisOutputTestInformationAggregator.class)
                                                   List<AnalysisOutputTestInformation> expectedInformationList)
            throws URISyntaxException, IOException {
        String content = Files.readString(Paths.get(getClass().getResource(String.format(EXAMPLE_FILE_PATH_FORMAT, filename)).toURI()));
        var actual = cut.analyseSecondaryStructureFile(structuralElementsHandling,
                visualizationTool,
                shouldRemoveIsolated,
                content,
                filename);
        AnalysisOutputTestUtils.assertAnalysisOutputs(actual, expectedInformationList);
    }

    @Test
    public void testSecondaryStructureFileAnalysisForWrongFileFormat() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> cut.analyseSecondaryStructureFile(StructuralElementsHandling.USE_PSEUDOKNOTS,
                        VisualizationTool.VARNA,
                        true,
                        "Mocked content",
                        "thisIsWrong.abc"),
                "Invalid attempt to analyze secondary structure for input type: .abc");
    }
}
