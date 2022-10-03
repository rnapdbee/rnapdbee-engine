package pl.poznan.put.rnapdbee.engine.calculation.logic;

import edu.put.rnapdbee.analysis.BasePairAnalyzer;
import edu.put.rnapdbee.analysis.RNApdbee;
import edu.put.rnapdbee.cache.AnalyzerCacheImpl;
import edu.put.rnapdbee.cache.ParserCacheImpl;
import edu.put.rnapdbee.enums.BasePairAnalyzerEnum;
import edu.put.rnapdbee.enums.ConverterEnum;
import edu.put.rnapdbee.enums.InputType;
import edu.put.rnapdbee.enums.NonCanonicalHandling;
import edu.put.rnapdbee.visualization.SecondaryStructureImage;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.consensus.BpSeqInfo;
import pl.poznan.put.consensus.ConsensusInput;
import pl.poznan.put.consensus.ConsensusOutput;
import pl.poznan.put.rnapdbee.engine.basepair.service.BasePairLoader;
import pl.poznan.put.rnapdbee.engine.calculation.mapper.AnalysisOutputsMapper;
import pl.poznan.put.rnapdbee.engine.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.model.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.model.ModelSelection;
import pl.poznan.put.rnapdbee.engine.model.OutputMulti;
import pl.poznan.put.rnapdbee.engine.model.OutputMultiEntry;
import pl.poznan.put.structure.formats.DotBracket;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service which purpose is to handle 3D -> Multi 2D analysis.
 */
@Component
public class ConsensusStructureAnalysisService {

    private final ImageService imageService;

    private final AnalysisOutputsMapper analysisOutputsMapper;

    private final BasePairLoader basePairLoader;

    // TODO: replace converter method with Mixed-Integer Linear Programming (separate Task)
    final static List<ConverterEnum> CONVERTERS = List.of(ConverterEnum.DPNEW);

    /**
     * Performs 3D -> multi 2D analysis.
     *
     * @param modelSelection      enum indicating if first, or all models from file should be analysed
     * @param includeNonCanonical boolean flag indicating if non-canonical pairs should be kept in analysis
     * @param removeIsolated      boolean flag indicating if isolated pairs should be removed from analysis
     * @param visualizationTool   enum indicating the tool/method that should be used in visualization
     * @param filename            name of the analysed file
     * @param content             content of the analysed file
     * @return output of the analysis
     */
    public OutputMulti analyse(ModelSelection modelSelection,
                               boolean includeNonCanonical,
                               boolean removeIsolated,
                               VisualizationTool visualizationTool,
                               String filename,
                               String content) {
        final var analyzerPairs = prepareAnalyzerPairs();
        final var consensus = findConsensus(modelSelection,
                includeNonCanonical, removeIsolated, filename, content, analyzerPairs);

        final List<BpSeqInfo> bpSeqInfos = consensus.getLeft().getBpSeqInfos();

        final List<OutputMultiEntry> outputMultiEntryList = bpSeqInfos.stream()
                .map(bpSeqInfo -> mapBpSeqInfoAndConsensusImageIntoOutputMultiEntry(
                        visualizationTool,
                        bpSeqInfo))
                .collect(Collectors.toList());

        return new OutputMulti()
                .withEntries(outputMultiEntryList);
    }

    private Pair<ConsensusInput, ConsensusOutput> findConsensus(ModelSelection modelSelection,
                                                                boolean includeNonCanonical,
                                                                boolean removeIsolated,
                                                                String filename,
                                                                String content,
                                                                Collection<Pair<BasePairAnalyzerEnum, BasePairAnalyzer>>
                                                                        analyzerPairs) {
        final Pair<ConsensusInput, ConsensusOutput> consensus;
        try {
            consensus = RNApdbee.findConsensus(
                    filename,
                    determineInputType(filename),
                    content,
                    analyzerPairs,
                    CONVERTERS,
                    includeNonCanonical ? NonCanonicalHandling.ANALYZE_VISUALIZE : NonCanonicalHandling.IGNORE,
                    removeIsolated,
                    // TODO: restore cache (best would be using Spring mechanisms) -> do with embedding of common codebase
                    new ParserCacheImpl(),
                    // TODO: restore cache (best would be using Spring mechanisms) -> do with embedding of common codebase
                    new AnalyzerCacheImpl(),
                    // TODO: ugly solution, but later will be better when rnapdbee-common code is embedded into engine
                    edu.put.rnapdbee.enums.ModelSelection.valueOf(modelSelection.toString()),
                    true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return consensus;
    }

    private InputType determineInputType(String filename) {
        for (InputType inputType: InputType.values()) {
            if (filename.contains(inputType.getFileExtension())) {
                return inputType;
            }
        }
        throw new IllegalArgumentException("unknown file extension provided");
    }

    private OutputMultiEntry mapBpSeqInfoAndConsensusImageIntoOutputMultiEntry(VisualizationTool visualizationTool,
                                                                               BpSeqInfo bpSeqInfo) {
        // TODO: using findFirst, because in future implementation using MILP the bpSeqInfo will only contain 1 dotBracket
        //  object, refactor when rnapdbee-common code is merged with the engine's code
        final DotBracket dotBracket = bpSeqInfo.uniqueDotBrackets().keySet()
                .stream().findFirst()
                .orElseThrow(RuntimeException::new);
        final SecondaryStructureImage secondaryVisualization = imageService.provideVisualization(visualizationTool, dotBracket);

        return analysisOutputsMapper.mapBpSeqInfoAndSecondaryStructureImageIntoOutputMultiEntry(bpSeqInfo, secondaryVisualization);
    }

    private Collection<Pair<BasePairAnalyzerEnum, BasePairAnalyzer>> prepareAnalyzerPairs() {
        return List.of(
                Pair.of(BasePairAnalyzerEnum.MCANNOTATE,
                        basePairLoader.provideBasePairAnalyzer(AnalysisTool.MC_ANNOTATE)),
                // TODO: fr3d is not always working
                // Pair.of(BasePairAnalyzerEnum.FR3D,
                //      basePairLoader.provideBasePairAnalyzer(AnalysisTool.FR3D_PYTHON)),
                // TODO: assuming 'DSSR' means barnaba ->
                //  must to be refactored when common code is joined with engine's code
                Pair.of(BasePairAnalyzerEnum.DSSR,
                        basePairLoader.provideBasePairAnalyzer(AnalysisTool.BARNABA)),
                // TODO: assuming 'FR3D' means BPNet ->
                //  must to be refactored when common code is joined with engine's code
                Pair.of(BasePairAnalyzerEnum.FR3D,
                        basePairLoader.provideBasePairAnalyzer(AnalysisTool.BPNET)),
                Pair.of(BasePairAnalyzerEnum.RNAVIEW,
                        basePairLoader.provideBasePairAnalyzer(AnalysisTool.RNAVIEW))
        );
    }

    @Autowired
    public ConsensusStructureAnalysisService(ImageService imageService,
                                             AnalysisOutputsMapper analysisOutputsMapper,
                                             BasePairLoader basePairLoader) {
        this.imageService = imageService;
        this.analysisOutputsMapper = analysisOutputsMapper;
        this.basePairLoader = basePairLoader;
    }
}
