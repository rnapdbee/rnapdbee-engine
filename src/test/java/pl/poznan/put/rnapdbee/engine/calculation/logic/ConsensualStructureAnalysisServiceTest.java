package pl.poznan.put.rnapdbee.engine.calculation.logic;

import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.consensual.ConsensualAnalysisTestInformation;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.consensual.ConsensualAnalysisTestInformationAggregator;
import pl.poznan.put.rnapdbee.engine.calculation.testhelp.consensual.ConsensualAnalysisTestUtils;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.ConsensualStructureAnalysisService;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.ModelSelection;

import java.util.List;

@SpringBootTest
@Import(AbstractTertiaryStructureAnalysisTestingClass.BeansReplacement.class)
class ConsensualStructureAnalysisServiceTest extends AbstractTertiaryStructureAnalysisTestingClass {

    @Autowired
    @InjectMocks
    ConsensualStructureAnalysisService cut;

    @ParameterizedTest
    @CsvFileSource(resources = "/3dToMulti2DTestCases.csv")
    @Timeout(60)
    void testConsensualAnalysis(String exampleFilename, ModelSelection modelSelection, boolean includeNonCanonical,
                                boolean removeIsolated, VisualizationTool visualizationTool,
                                @AggregateWith(ConsensualAnalysisTestInformationAggregator.class)
                                List<ConsensualAnalysisTestInformation> expectedInformationList) {
        prepareMockWebServerStubs(exampleFilename);
        String fileContent = readFileContentFromFile(exampleFilename);
        var result = cut.analyze(modelSelection, includeNonCanonical, removeIsolated, visualizationTool, exampleFilename, fileContent);
        // TODO: add assertions for adapterEnums when rnapdbee-common code is merged with rnapdbee-engine
        ConsensualAnalysisTestUtils.assertAnalysisOutput(result, expectedInformationList);
    }

    ConsensualStructureAnalysisServiceTest() {
        EXAMPLE_PDB_FILE_PATH_FORMAT = "/3DToMulti2DMocks/%s/pdbfile.pdb";
        EXAMPLE_CIF_FILE_PATH_FORMAT = "/3DToMulti2DMocks/%s/mmciffile.cif";

        BARNABA_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/barnaba_response.json";
        BPNET_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/bpnet_response.json";
        MC_ANNOTATE_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/mc_annotate_response.json";
        RNAVIEW_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/rnaview_response.json";
    }
}
