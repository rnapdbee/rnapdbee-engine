package pl.poznan.put.rnapdbee.engine.calculation.consensus;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.BPNetBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.BarnabaBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.Fr3dBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.MCAnnotateBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.RnaViewBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.RnapolisBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.service.BasePairAnalyzerFactory;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.testhelp.shared.AbstractTertiaryStructureAnalysisTestingClass;
import pl.poznan.put.rnapdbee.engine.testhelp.consensual.ConsensualAnalysisTestInformation;
import pl.poznan.put.rnapdbee.engine.testhelp.consensual.ConsensualAnalysisTestInformationAggregator;
import pl.poznan.put.rnapdbee.engine.testhelp.consensual.ConsensualAnalysisTestUtils;
import pl.poznan.put.rnapdbee.engine.testhelp.shared.configuration.TestConverterConfiguration;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.ModelSelection;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        AbstractTertiaryStructureAnalysisTestingClass.BeansReplacement.class,
        TestConverterConfiguration.class})
class ConsensualStructureAnalysisServiceIntegrationTest extends AbstractTertiaryStructureAnalysisTestingClass {

    @Autowired
    ConsensualStructureAnalysisService cut;

    @Autowired
    BarnabaBasePairAnalyzer barnabaBasePairAnalyzer;
    @Autowired
    BPNetBasePairAnalyzer bpNetBasePairAnalyzer;
    @Autowired
    MCAnnotateBasePairAnalyzer mcAnnotateBasePairAnalyzer;
    @Autowired
    RnaViewBasePairAnalyzer rnaViewBasePairAnalyzer;
    @Autowired
    RnapolisBasePairAnalyzer rnapolisBasePairAnalyzer;
    @Autowired
    Fr3dBasePairAnalyzer fr3dBasePairAnalyzer;

    @MockBean
    BasePairAnalyzerFactory basePairAnalyzerFactory;

    @ParameterizedTest
    @CsvFileSource(resources = "/3dToMulti2DTestCases.csv")
    @Timeout(60)
    void testConsensualAnalysis(String exampleFilename, ModelSelection modelSelection, boolean includeNonCanonical,
                                boolean removeIsolated, VisualizationTool visualizationTool,
                                @AggregateWith(ConsensualAnalysisTestInformationAggregator.class)
                                List<ConsensualAnalysisTestInformation> expectedInformationList) {
        when(basePairAnalyzerFactory.prepareAnalyzerPairs())
                .thenReturn(List.of(
                        Pair.of(AnalysisTool.MC_ANNOTATE, mcAnnotateBasePairAnalyzer),
                        Pair.of(AnalysisTool.FR3D_PYTHON, fr3dBasePairAnalyzer),
                        Pair.of(AnalysisTool.BARNABA, barnabaBasePairAnalyzer),
                        Pair.of(AnalysisTool.BPNET, bpNetBasePairAnalyzer),
                        Pair.of(AnalysisTool.RNAVIEW, rnaViewBasePairAnalyzer),
                        Pair.of(AnalysisTool.RNAPOLIS, rnapolisBasePairAnalyzer)
                ));
        prepareMockWebServerStubs(exampleFilename);
        String fileContent = readFileContentFromFile(exampleFilename);
        var result = cut.analyze(modelSelection, includeNonCanonical, removeIsolated, visualizationTool, exampleFilename, fileContent);
        // TODO: add assertions for adapterEnums when rnapdbee-common code is merged with rnapdbee-engine
        ConsensualAnalysisTestUtils.assertAnalysisOutput(result, expectedInformationList);
    }

    ConsensualStructureAnalysisServiceIntegrationTest() {
        EXAMPLE_PDB_FILE_PATH_FORMAT = "/3DToMulti2DMocks/%s/pdbfile.pdb";
        EXAMPLE_CIF_FILE_PATH_FORMAT = "/3DToMulti2DMocks/%s/mmciffile.cif";

        BARNABA_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/barnaba_response.json";
        BPNET_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/bpnet_response.json";
        MC_ANNOTATE_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/mc_annotate_response.json";
        RNAVIEW_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/rnaview_response.json";
        RNAPOLIS_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/rnapolis_response.json";
        FR3D_RESPONSE_MOCK_PATH_FORMAT = "/3DToMulti2DMocks/%s/fr3d_response.json";
    }
}
