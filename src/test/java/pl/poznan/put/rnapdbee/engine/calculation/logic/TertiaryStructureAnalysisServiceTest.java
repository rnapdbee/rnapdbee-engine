package pl.poznan.put.rnapdbee.engine.calculation.logic;

import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.tertiary.TertiaryAnalysisOutputTestInformation;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.tertiary.TertiaryAnalysisOutputTestInformationAggregator;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.tertiary.TertiaryAnalysisOutputTestUtils;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.ModelSelection;
import pl.poznan.put.rnapdbee.engine.shared.domain.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.engine.calculation.tertiary.TertiaryStructureAnalysisService;

import java.util.List;


@SpringBootTest
@Import(AbstractTertiaryStructureAnalysisTestingClass.BeansReplacement.class)
class TertiaryStructureAnalysisServiceTest extends AbstractTertiaryStructureAnalysisTestingClass {

    @Autowired
    TertiaryStructureAnalysisService cut;

    @ParameterizedTest
    @CsvFileSource(resources = "/3dToSecondaryTestCases.csv")
    @Timeout(60)
    void testTertiaryToDotBracketNotationAnalysis(String exampleFilename,
                                                  ModelSelection modelSelection,
                                                  AnalysisTool analysisTool,
                                                  NonCanonicalHandling nonCanonicalHandling,
                                                  boolean removeIsolated,
                                                  StructuralElementsHandling structuralElementsHandling,
                                                  VisualizationTool visualizationTool,
                                                  @AggregateWith(TertiaryAnalysisOutputTestInformationAggregator.class)
                                                  List<TertiaryAnalysisOutputTestInformation> expectedInformationList) {
        prepareMockWebServerStubs(exampleFilename);
        String fileContent = readFileContentFromFile(exampleFilename);
        var result = cut.analyze(modelSelection,
                analysisTool,
                nonCanonicalHandling,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                exampleFilename,
                fileContent);
        TertiaryAnalysisOutputTestUtils.assertAnalysisOutputs(result, expectedInformationList);
    }

    TertiaryStructureAnalysisServiceTest() {
        EXAMPLE_PDB_FILE_PATH_FORMAT = "/3DToSecondaryMocks/%s/pdbfile.pdb";
        EXAMPLE_CIF_FILE_PATH_FORMAT = "/3DToSecondaryMocks/%s/mmciffile.cif";

        BARNABA_RESPONSE_MOCK_PATH_FORMAT = "/3DToSecondaryMocks/%s/mocked_response.json";
        BPNET_RESPONSE_MOCK_PATH_FORMAT = "/3DToSecondaryMocks/%s/bpnet_response.json";
        MC_ANNOTATE_RESPONSE_MOCK_PATH_FORMAT = "/3DToSecondaryMocks/%s/mc_annotate_response.json";
        RNAVIEW_RESPONSE_MOCK_PATH_FORMAT = "/3DToSecondaryMocks/%s/rnaview_response.json";
    }
}