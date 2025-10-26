package pl.poznan.put.rnapdbee.engine.calculation.tertiary;

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
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.BasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.Fr3dBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.MCAnnotateBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.RnaViewBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.RnapolisBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.MaxitBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.service.BasePairAnalyzerFactory;
import pl.poznan.put.rnapdbee.engine.testhelp.shared.AbstractTertiaryStructureAnalysisTestingClass;
import pl.poznan.put.rnapdbee.engine.testhelp.shared.configuration.TestConverterConfiguration;
import pl.poznan.put.rnapdbee.engine.testhelp.tertiary.TertiaryAnalysisOutputTestInformation;
import pl.poznan.put.rnapdbee.engine.testhelp.tertiary.TertiaryAnalysisOutputTestInformationAggregator;
import pl.poznan.put.rnapdbee.engine.testhelp.tertiary.TertiaryAnalysisOutputTestUtils;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.ModelSelection;
import pl.poznan.put.rnapdbee.engine.shared.domain.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementsHandling;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        AbstractTertiaryStructureAnalysisTestingClass.BeansReplacement.class,
        TestConverterConfiguration.class })
class TertiaryStructureAnalysisServiceIntegrationTest extends AbstractTertiaryStructureAnalysisTestingClass {

    @Autowired
    TertiaryStructureAnalysisService cut;

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
    @Autowired
    MaxitBasePairAnalyzer maxitBasePairAnalyzer;

    @MockBean
    BasePairAnalyzerFactory basePairAnalyzerFactory;

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
            @AggregateWith(TertiaryAnalysisOutputTestInformationAggregator.class) List<TertiaryAnalysisOutputTestInformation> expectedInformationList) {
        when(basePairAnalyzerFactory.provideBasePairAnalyzer(analysisTool))
                .thenReturn(prepareMockBasePairAnalyzer(analysisTool));
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

    private BasePairAnalyzer prepareMockBasePairAnalyzer(AnalysisTool analysisTool) {
        switch (analysisTool) {
            case BPNET:
                return bpNetBasePairAnalyzer;
            case BARNABA:
                return barnabaBasePairAnalyzer;
            case RNAVIEW:
                return rnaViewBasePairAnalyzer;
            case FR3D_PYTHON:
                return fr3dBasePairAnalyzer;
            case MC_ANNOTATE:
                return mcAnnotateBasePairAnalyzer;
            case RNAPOLIS:
                return rnapolisBasePairAnalyzer;
            case MAXIT:
                return maxitBasePairAnalyzer;
            default:
                throw new IllegalArgumentException("unhandled enum passed to provideBasePairAnalyzer method");
        }
    }

    TertiaryStructureAnalysisServiceIntegrationTest() {
        EXAMPLE_PDB_FILE_PATH_FORMAT = "/3DToSecondaryMocks/%s/pdbfile.pdb";
        EXAMPLE_CIF_FILE_PATH_FORMAT = "/3DToSecondaryMocks/%s/mmciffile.cif";

        BARNABA_RESPONSE_MOCK_PATH_FORMAT = "/3DToSecondaryMocks/%s/mocked_response.json";
        BPNET_RESPONSE_MOCK_PATH_FORMAT = "/3DToSecondaryMocks/%s/bpnet_response.json";
        MC_ANNOTATE_RESPONSE_MOCK_PATH_FORMAT = "/3DToSecondaryMocks/%s/mc_annotate_response.json";
        RNAVIEW_RESPONSE_MOCK_PATH_FORMAT = "/3DToSecondaryMocks/%s/rnaview_response.json";
        RNAPOLIS_RESPONSE_MOCK_PATH_FORMAT = "/3DToSecondaryMocks/%s/rnapolis_response.json";
        FR3D_RESPONSE_MOCK_PATH_FORMAT = "/3DToSecondaryMocks/%s/fr3d_response.json";
        MAXIT_RESPONSE_MOCK_PATH_FORMAT = "/3DToSecondaryMocks/%s/maxit_response.json";
    }
}
