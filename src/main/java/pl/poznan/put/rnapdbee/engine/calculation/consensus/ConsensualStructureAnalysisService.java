package pl.poznan.put.rnapdbee.engine.calculation.consensus;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.ConsensualVisualization;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.visualization.boundary.ConsensualVisualizationDrawer;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMulti;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.Output2D;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.BasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;
import pl.poznan.put.rnapdbee.engine.shared.basepair.service.BasePairAnalyzerFactory;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputType;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputTypeDeterminer;
import pl.poznan.put.rnapdbee.engine.shared.domain.ModelSelection;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementOutput;
import pl.poznan.put.rnapdbee.engine.shared.exception.AdaptersErrorException;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.shared.parser.TertiaryFileParser;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.ImmutableDefaultDotBracketFromPdb;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service which purpose is to handle 3D -> Multi 2D analysis.
 */
@Component
public class ConsensualStructureAnalysisService {

    private final Logger logger = LoggerFactory.getLogger(ConsensualStructureAnalysisService.class);

    private final ImageService imageService;
    private final TertiaryFileParser tertiaryFileParser;
    private final BasePairAnalyzerFactory basePairAnalyzerFactory;
    private final InputTypeDeterminer inputTypeDeterminer;
    private final Converter converter;
    private final ConsensualVisualizationDrawer consensualVisualizationDrawer;

    /**
     * Performs 3D -> multi 2D analysis.
     *
     * @param modelSelection      enum indicating if first, or all models from file should be analyzed
     * @param includeNonCanonical boolean flag indicating if non-canonical pairs should be kept in analysis
     * @param removeIsolated      boolean flag indicating if isolated pairs should be removed from analysis
     * @param visualizationTool   enum indicating the tool/method that should be used in visualization
     * @param filename            name of the analyzed file
     * @param content             content of the analyzed file
     * @return output of the analysis
     */
    public OutputMulti analyze(final ModelSelection modelSelection,
                               final boolean includeNonCanonical,
                               final boolean removeIsolated,
                               final VisualizationTool visualizationTool,
                               final String filename,
                               final String content) {
        final var analyzerPairs = basePairAnalyzerFactory.prepareAnalyzerPairs();
        final var inputType = inputTypeDeterminer.detectTertiaryInputTypeFromFileName(filename);

        return findConsensus(modelSelection,
                inputType,
                content,
                analyzerPairs,
                includeNonCanonical,
                removeIsolated,
                visualizationTool);
    }

    private OutputMulti findConsensus(
            final ModelSelection modelSelection,
            final InputType inputType,
            final String fileContents,
            final Collection<? extends Pair<AnalysisTool, BasePairAnalyzer>> analyzerPairs,
            final boolean includeNonCanonical,
            final boolean removeIsolated,
            final VisualizationTool visualizationTool) {
        AtomicReference<String> title = new AtomicReference<>("");

        final List<? extends PdbModel> models = tertiaryFileParser.parseFileContents(inputType, fileContents);

        final int modelsToBeProcessed = modelSelection == ModelSelection.FIRST
                ? 1
                : models.size();

        final Map<BpSeq, OutputMultiEntry> uniqueInputs = new LinkedHashMap<>();
        models.stream()
                .limit(modelsToBeProcessed)
                .filter(pdbModel -> pdbModel.containsAny(MoleculeType.RNA))
                .forEach(model -> {
                    final PdbModel rna = model.filteredNewInstance(MoleculeType.RNA);
                    title.set(rna.title());
                    final int modelNumber = rna.modelNumber();
                    // TODO: unit test this behaviour.
                    analyzerPairs.forEach(analyzerPair -> {
                        try {
                            performSingularAnalysis(analyzerPair, fileContents,
                                    includeNonCanonical, removeIsolated, visualizationTool, uniqueInputs, rna, modelNumber);
                        } catch (AdaptersErrorException e) {
                            logger.warn(String.format(
                                    "Adapters error received when analysing structure number %s with adapter %s, nonetheless continuing",
                                    modelNumber, analyzerPair.getLeft()));
                        }
                    });
                });

        List<OutputMultiEntry> outputMultiEntries = new ArrayList<>(uniqueInputs.values());
        byte[] visualization = consensualVisualizationDrawer.performVisualization(outputMultiEntries);

        return new OutputMulti.OutputMultiBuilder()
                .withEntries(outputMultiEntries)
                .withConsensualVisualization(new ConsensualVisualization(visualization))
                .withTitle(title.get())
                .build();
    }

    private void performSingularAnalysis(final Pair<AnalysisTool, BasePairAnalyzer> analyzerPair,
                                         final String fileContents,
                                         final boolean includeNonCanonical,
                                         final boolean removeIsolated,
                                         final VisualizationTool visualizationTool,
                                         final Map<BpSeq, OutputMultiEntry> uniqueInputs,
                                         final PdbModel rna,
                                         final int modelNumber) throws AdaptersErrorException {
        final AnalysisTool analyzerEnum = analyzerPair.getLeft();
        final BasePairAnalyzer analyzer = analyzerPair.getRight();
        // TODO: maybe analyze should return optional instead of throwing exception?
        final BasePairAnalysis analysisResults =
                analyzer.analyze(fileContents, includeNonCanonical, modelNumber);
        if (removeIsolated) {
            analysisResults.removeIsolatedBasePairs(rna);
        }

        final List<AnalyzedBasePair> represented = analysisResults.getRepresented();
        final BpSeq bpSeq = BpSeq.fromBasePairs(rna.namedResidueIdentifiers(), represented);

        if (uniqueInputs.containsKey(bpSeq)) {
            final OutputMultiEntry bpSeqInfo = uniqueInputs.get(bpSeq);
            bpSeqInfo.getAdapterEnums().add(analyzerEnum);
            return;
        }

        final Ct ct = Ct.fromBpSeqAndPdbModel(bpSeq, rna);
        final DotBracket convertedDotBracket = converter.convert(bpSeq);
        final DotBracket dotBracketFromPdb = ImmutableDefaultDotBracketFromPdb
                .of(convertedDotBracket.sequence(), convertedDotBracket.structure(), rna);

        final ImageInformationOutput imageInformation = imageService.visualizeCanonical(visualizationTool,
                dotBracketFromPdb);
        final List<AnalysisTool> analyzerNames = new ArrayList<>();
        analyzerNames.add(analyzerEnum);

        final Output2D output2D = new Output2D.Output2DBuilder()
                .withImageInformation(imageInformation)
                .withBpSeqFromBpSeqObject(bpSeq)
                .withStrandsFromDotBracket(dotBracketFromPdb)
                .withCtFromCt(ct)
                .withStructuralElement(StructuralElementOutput.EMPTY_INSTANCE)
                .build();

        final OutputMultiEntry outputMultiEntry = new OutputMultiEntry.OutputMultiEntryBuilder()
                .withOutput2D(output2D)
                .withAdapterEnums(analyzerNames)
                .build();

        uniqueInputs.put(bpSeq, outputMultiEntry);
    }

    @Autowired
    public ConsensualStructureAnalysisService(ImageService imageService,
                                              TertiaryFileParser tertiaryFileParser,
                                              BasePairAnalyzerFactory basePairAnalyzerFactory,
                                              InputTypeDeterminer inputTypeDeterminer,
                                              Converter converter,
                                              ConsensualVisualizationDrawer consensualVisualizationDrawer) {
        this.imageService = imageService;
        this.tertiaryFileParser = tertiaryFileParser;
        this.basePairAnalyzerFactory = basePairAnalyzerFactory;
        this.inputTypeDeterminer = inputTypeDeterminer;
        this.converter = converter;
        this.consensualVisualizationDrawer = consensualVisualizationDrawer;
    }
}
