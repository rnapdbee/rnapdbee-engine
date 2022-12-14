package pl.poznan.put.rnapdbee.engine.calculation.secondary;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import pl.poznan.put.rnapdbee.engine.testhelp.secondary.SecondaryAnalysisOutputTestInformation;
import pl.poznan.put.rnapdbee.engine.testhelp.secondary.SecondaryAnalysisOutputTestInformationAggregator;
import pl.poznan.put.rnapdbee.engine.testhelp.secondary.SecondaryAnalysisOutputTestUtils;
import pl.poznan.put.rnapdbee.engine.testhelp.shared.configuration.TestConverterConfiguration;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementsHandling;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;


@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConverterConfiguration.class)
class SecondaryStructureAnalysisServiceTest {

    static String EXAMPLE_FILE_PATH_FORMAT = "/secondaryTestFiles/%s";

    @MockBean
    ImageService imageService;

    @Autowired
    SecondaryStructureAnalysisService cut;

    @BeforeEach
    void provideMocks() {
        ImageInformationOutput imageMock = Mockito.mock(ImageInformationOutput.class);
        Mockito.when(imageService.visualizeCanonical(Mockito.any(), Mockito.any())).thenReturn(imageMock);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/secondaryToDotBracketTestCases.csv", maxCharsPerColumn = 16384)
    public void testSecondaryStructureFileAnalysis(StructuralElementsHandling structuralElementsHandling,
                                                   VisualizationTool visualizationTool,
                                                   String filename,
                                                   boolean shouldRemoveIsolated,
                                                   @AggregateWith(SecondaryAnalysisOutputTestInformationAggregator.class)
                                                       SecondaryAnalysisOutputTestInformation expectedInformationList)
            throws URISyntaxException, IOException {
        String content = Files.readString(Paths.get(getClass().getResource(String.format(EXAMPLE_FILE_PATH_FORMAT, filename)).toURI()));
        var actual = cut.analyzeSecondaryStructureFile(structuralElementsHandling,
                visualizationTool,
                shouldRemoveIsolated,
                content,
                filename);
        SecondaryAnalysisOutputTestUtils.assertAnalysisOutputs(actual, expectedInformationList);
    }

    @Test
    public void testSecondaryStructureFileAnalysisForWrongFileFormat() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> cut.analyzeSecondaryStructureFile(StructuralElementsHandling.USE_PSEUDOKNOTS,
                        VisualizationTool.VARNA,
                        true,
                        "Mocked content",
                        "thisIsWrong.abc"),
                "Invalid attempt to analyze secondary structure for input type: .abc");
    }
}
