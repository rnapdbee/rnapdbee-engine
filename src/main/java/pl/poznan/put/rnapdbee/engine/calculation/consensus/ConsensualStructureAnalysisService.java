package pl.poznan.put.rnapdbee.engine.calculation.consensus;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.ConsensualVisualization;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMulti;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.Output2D;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.BasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;
import pl.poznan.put.rnapdbee.engine.shared.basepair.exception.AdaptersErrorException;
import pl.poznan.put.rnapdbee.engine.shared.basepair.service.BasePairAnalyzerFactory;
import pl.poznan.put.rnapdbee.engine.shared.domain.*;
import pl.poznan.put.rnapdbee.engine.shared.exception.ConsensualVisualizationException;
import pl.poznan.put.rnapdbee.engine.shared.exception.NoRnaModelsInFileException;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.image.exception.VisualizationException;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.ConsensualVisualizationDrawer;
import pl.poznan.put.rnapdbee.engine.shared.parser.TertiaryFileParser;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.formats.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Service which purpose is to handle 3D -> Multi 2D analysis.
 */
@Component
public class ConsensualStructureAnalysisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsensualStructureAnalysisService.class);

    private static final String BASE_PAIR_ANALYSIS_FAILED =
            "Base pair analysis failed.";
    private static final String BASE_PAIR_ANALYSIS_FAILED_DEBUG_FORMAT =
            "Base pair analysis failed for visualizationTool %s, includeNonCanonical %s and modelNumber %s." +
                    " Continuing analysis for other models & adapters";
    private static final String BIOCOMMONS_ERROR_MET =
            "BioCommons exception met when creating models out of base pair analysis.";
    private static final String BIOCOMMONS_ERROR_MET_DEBUG_FORMAT =
            "BioCommons exception met when creating models out of base pair analysis for visualizationTool %s, " +
                    "includeNonCanonical %s and modelNumber %s. Continuing analysis for other models & adapters.";

    private final ImageService imageService;
    private final TertiaryFileParser tertiaryFileParser;
    private final BasePairAnalyzerFactory basePairAnalyzerFactory;
    private final InputTypeDeterminer inputTypeDeterminer;
    private final Converter converter;
    private final ConsensualVisualizationDrawer consensualVisualizationDrawer;

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

        final List<? extends PdbModel> models = tertiaryFileParser.parseFileContents(inputType, fileContents);
        final List<? extends PdbModel> rnaModels = models.stream()
                .filter(pdbModel -> pdbModel.containsAny(MoleculeType.RNA)).collect(Collectors.toList());
        if (rnaModels.isEmpty()) {
            throw new NoRnaModelsInFileException();
        }

        AtomicReference<String> title = new AtomicReference<>("");
        final Map<BpSeq, OutputMultiEntry> uniqueInputs = new LinkedHashMap<>();
        final int modelsToBeProcessed = modelSelection == ModelSelection.FIRST
                ? 1
                : rnaModels.size();
        rnaModels.stream()
                .limit(modelsToBeProcessed)
                .forEach(model -> {
                    final PdbModel rna = model.filteredNewInstance(MoleculeType.RNA);
                    title.set(rna.title());
                    analyzerPairs.forEach(analyzerPair -> performSingularAnalysis(analyzerPair, fileContents,
                            includeNonCanonical, removeIsolated, visualizationTool, uniqueInputs, rna));
                });

        List<OutputMultiEntry> outputMultiEntries = new ArrayList<>(uniqueInputs.values());
        byte[] visualization;

        try {
            visualization = consensualVisualizationDrawer.performVisualization(outputMultiEntries);
        } catch (VisualizationException e) {
            throw new ConsensualVisualizationException();
        }

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
                                         final PdbModel rna) {
        final AnalysisTool analyzerEnum = analyzerPair.getLeft();
        final BasePairAnalyzer analyzer = analyzerPair.getRight();
        final BasePairAnalysis analysisResults;

        try {
            analysisResults = analyzer.analyze(fileContents, includeNonCanonical, rna);
        } catch (AdaptersErrorException exception) {
            LOGGER.warn(BASE_PAIR_ANALYSIS_FAILED, exception);
            LOGGER.debug(String.format(BASE_PAIR_ANALYSIS_FAILED_DEBUG_FORMAT, visualizationTool, includeNonCanonical,
                            rna.modelNumber()),
                    exception);
            return;
        }
        if (removeIsolated) {
            analysisResults.removeIsolatedBasePairs(rna);
        }

        final List<AnalyzedBasePair> represented = analysisResults.getRepresented();
        final BpSeq bpSeq;
        try {
            bpSeq = BpSeq.fromBasePairs(rna.namedResidueIdentifiers(), represented);
        } catch (IllegalArgumentException exception) {
            LOGGER.error(BIOCOMMONS_ERROR_MET, exception);
            LOGGER.debug(String.format(BIOCOMMONS_ERROR_MET_DEBUG_FORMAT, visualizationTool, includeNonCanonical,
                            rna.modelNumber()),
                    exception);
            return;
        }

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
}
