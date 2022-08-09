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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.consensus.BpSeqInfo;
import pl.poznan.put.consensus.ConsensusInput;
import pl.poznan.put.consensus.ConsensusOutput;
import pl.poznan.put.rnapdbee.engine.basepair.boundary.MCAnnotateBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.model.ModelSelection;
import pl.poznan.put.rnapdbee.engine.model.OutputMulti;
import pl.poznan.put.rnapdbee.engine.model.OutputMultiEntry;
import pl.poznan.put.structure.formats.DotBracket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
public class ConsensualStructureAnalysisService {

    @Autowired
    ImageService imageService;

    @Autowired
    BasePairAnalyserLoader basePairAnalyserLoader;

    @Autowired
    private ApplicationContext context;

    // TODO: when embedding RNAPDBEE-common code, replace with calls to rnapdbee-adapters
    List<BasePairAnalyzerEnum> basePairAnalyzers = List.of(BasePairAnalyzerEnum.RNAVIEW, BasePairAnalyzerEnum.MCANNOTATE,
            BasePairAnalyzerEnum.DSSR, BasePairAnalyzerEnum.FR3D);

    // TODO: replace converter method with Mixed-Integer Linear Programming (separate Task)
    final List<ConverterEnum> CONVERTERS = List.of(ConverterEnum.DPNEW, ConverterEnum.EG);

    public OutputMulti analyse(ModelSelection modelSelection,
                               boolean includeNonCanonical,
                               boolean removeIsolated,
                               VisualizationTool visualizationTool,
                               String filename,
                               String content) {
        // TODO: when incorporating adapters into engine, this should be done using rnapdbee-adapters.
        final Collection<Pair<BasePairAnalyzerEnum, BasePairAnalyzer>> analyzerPairs =
               /* basePairAnalyzers.stream().map(analyzerEnum -> {
                    try {
                        return Pair.of(analyzerEnum, basePairAnalyserLoader.loadBasePairAnalyzer(analyzerEnum, false));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());*/
                List.of(Pair.of(BasePairAnalyzerEnum.MCANNOTATE, context.getBean(MCAnnotateBasePairAnalyzer.class)));

        final Pair<ConsensusInput, ConsensusOutput> consensus;
        try {
            consensus = RNApdbee.findConsensus(
                    filename,
                    InputType.valueOf(filename.split("\\.")[1].toUpperCase()),
                    content,
                    analyzerPairs,
                    CONVERTERS,
                    includeNonCanonical ? NonCanonicalHandling.ANALYZE_VISUALIZE : NonCanonicalHandling.IGNORE,
                    removeIsolated,
                    // TODO: restore cache (best would be using Spring mechanisms)
                    new ParserCacheImpl(),
                    // TODO: restore cache (best would be using Spring mechanisms)
                    new AnalyzerCacheImpl(),
                    // TODO: ugly solution, but later will be better when rnapdbee-common code is embedded into engine
                    edu.put.rnapdbee.enums.ModelSelection.valueOf(modelSelection.toString()),
                    true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final List<BpSeqInfo> bpSeqInfos = consensus.getLeft().getBpSeqInfos();
        final List<SVGDocument> svgDocuments = consensus.getRight().getSvgDocuments();
        final int size = svgDocuments.size();
        assert bpSeqInfos.size() == svgDocuments.size();

        final List<OutputMultiEntry> results = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            // probably not needed
            /* final ConsensusParameters parameters = new ConsensusParameters();
            parameters.copy(formData.getParameters()); */
            final BpSeqInfo bpSeqInfo = bpSeqInfos.get(i);

            final Set<DotBracket> dotBrackets = bpSeqInfo.uniqueDotBrackets().keySet();
            final List<SecondaryStructureImage> imageUrls = new ArrayList<>(dotBrackets.size());
            for (final DotBracket dotBracket : dotBrackets) {
                imageUrls.add(imageService.provideVisualization(visualizationTool, dotBracket));
            }

            /*results.add(
                    new ConsensusResult(
                            parameters, bpSeqInfo, imageUrls, image, svgUrl, pngUrl, bpSeqInfo.getTitle()));*/
        }

        return new OutputMulti();
    }
}
