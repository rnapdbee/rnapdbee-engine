package pl.poznan.put.rnapdbee.engine.calculation.consensus;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMulti;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.BasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;
import pl.poznan.put.rnapdbee.engine.shared.basepair.exception.AdaptersErrorException;
import pl.poznan.put.rnapdbee.engine.shared.basepair.service.BasePairAnalyzerFactory;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputType;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputTypeDeterminer;
import pl.poznan.put.rnapdbee.engine.shared.domain.ModelSelection;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.ConsensualVisualizationDrawer;
import pl.poznan.put.rnapdbee.engine.shared.parser.TertiaryFileParser;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DotBracket;

import java.util.List;

import static org.mockito.Mockito.mockStatic;


@ExtendWith(MockitoExtension.class)
class ConsensualStructureAnalysisServiceTest {

    @Mock
    ImageService imageService;
    @Mock
    TertiaryFileParser tertiaryFileParser;
    @Mock
    BasePairAnalyzerFactory basePairAnalyzerFactory;
    @Mock
    InputTypeDeterminer inputTypeDeterminer;
    @Mock
    Converter converter;
    @Mock
    ConsensualVisualizationDrawer consensualVisualizationDrawer;

    @InjectMocks
    ConsensualStructureAnalysisService cut;

    private static final String MOCKED_FILE_NAME = "";
    private static final String MOCKED_CONTENT = "";

    @Test
    void shouldContinueConsensualAnalysisIfBasePairAnalysisThrewAdaptersErrorException() throws AdaptersErrorException {
        // mocked
        PdbModel mockedModel = mockPdbModel();

        InputType mockedInputType = InputType.PDB;
        Mockito.when(inputTypeDeterminer.detectTertiaryInputTypeFromFileName(MOCKED_FILE_NAME))
                .thenReturn(mockedInputType);
        Mockito.when(tertiaryFileParser.parseFileContents(mockedInputType, MOCKED_CONTENT))
                .thenAnswer(x -> List.of(mockedModel));
        DotBracket mockedDotBracket = mockDotBracket();
        Mockito.when(converter.convert(Mockito.any()))
                .thenReturn(mockedDotBracket);

        BasePairAnalyzer successfulAnalyzer = Mockito.mock(BasePairAnalyzer.class);
        BasePairAnalyzer failingAnalyzer = Mockito.mock(BasePairAnalyzer.class);

        Mockito.when(successfulAnalyzer.analyze(MOCKED_CONTENT, true, 0))
                .thenReturn(Mockito.mock(BasePairAnalysis.class));
        Mockito.when(failingAnalyzer.analyze(MOCKED_CONTENT, true, 0))
                .thenThrow(new AdaptersErrorException("Some error"));

        Mockito.when(basePairAnalyzerFactory.prepareAnalyzerPairs()).thenReturn(List.of(
                Pair.of(AnalysisTool.RNAPOLIS, successfulAnalyzer),
                Pair.of(AnalysisTool.FR3D_PYTHON, failingAnalyzer)
        ));

        try (MockedStatic<Ct> theMock = mockStatic(Ct.class)) {
            theMock.when(() -> Ct.fromBpSeqAndPdbModel(Mockito.any(), Mockito.any()))
                    .thenReturn(Mockito.mock(Ct.class));
            // when
            var result = cut.analyze(ModelSelection.FIRST,
                    true,
                    true,
                    VisualizationTool.NONE,
                    MOCKED_FILE_NAME,
                    MOCKED_CONTENT
            );
            // then
            Assertions.assertThat(result)
                    .extracting(OutputMulti::getEntries)
                    .extracting(List::size)
                    .isEqualTo(1);
        }
    }

    private static PdbModel mockPdbModel() {

        PdbModel mockedModel = Mockito.mock(PdbModel.class);
        PdbModel mockedRnaModel = Mockito.mock(PdbModel.class);
        Mockito.when(mockedModel.containsAny(MoleculeType.RNA)).thenReturn(true);
        Mockito.when(mockedModel.filteredNewInstance(MoleculeType.RNA))
                .thenReturn(mockedRnaModel);
        return mockedModel;
    }

    private static DotBracket mockDotBracket() {
        DotBracket mockedDotBracket = Mockito.mock(DotBracket.class);
        Mockito.when(mockedDotBracket.sequence()).thenReturn("AAAA");
        Mockito.when(mockedDotBracket.structure()).thenReturn(".().");
        return mockedDotBracket;
    }
}
