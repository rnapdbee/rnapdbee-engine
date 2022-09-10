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
import pl.poznan.put.rnapdbee.engine.calculation.mapper.AnalysisOutputsMapper;
import pl.poznan.put.rnapdbee.engine.calculation.model.Output2D;
import pl.poznan.put.rnapdbee.engine.calculation.model.SingleSecondaryModelAnalysisOutput;
import pl.poznan.put.rnapdbee.engine.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.image.logic.ImageUtils;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.model.ConsensusVisualization;
import pl.poznan.put.rnapdbee.engine.model.ModelSelection;
import pl.poznan.put.rnapdbee.engine.model.OutputMulti;
import pl.poznan.put.rnapdbee.engine.model.OutputMultiEntry;
import pl.poznan.put.structure.formats.DotBracket;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ConsensusStructureAnalysisService {

    private final ImageService imageService;

    private final AnalysisOutputsMapper analysisOutputsMapper;

    private final ServletContext servletContext;

    private final MCAnnotateBasePairAnalyzer mcAnnotateBasePairAnalyzer;

    // TODO: replace converter method with Mixed-Integer Linear Programming (separate Task)
    final static List<ConverterEnum> CONVERTERS = List.of(ConverterEnum.DPNEW);

    public OutputMulti analyse(ModelSelection modelSelection,
                               boolean includeNonCanonical,
                               boolean removeIsolated,
                               VisualizationTool visualizationTool,
                               String filename,
                               String content) {
        final Collection<Pair<BasePairAnalyzerEnum, BasePairAnalyzer>> analyzerPairs =
                List.of(
                        Pair.of(BasePairAnalyzerEnum.MCANNOTATE, mcAnnotateBasePairAnalyzer)
                        // TODO: fr3d is not always working - saengers are null
                        // Pair.of(BasePairAnalyzerEnum.FR3D, context.getBean(Fr3dBasePairAnalyzer.class)),
                        // TODO: assuming DSSR means barnaba ->
                        //  must to be refactored when common code is joined with engine's code
                        // TODO: barnaba is not always working - saengers are null
                        // Pair.of(BasePairAnalyzerEnum.DSSR, context.getBean(BarnabaBasePairAnalyzer.class))
                        // TODO: assuming RNAVIEW means BPNet ->
                        //  must to be refactored when common code is joined with engine's code
                        // TODO: bpnet throws Inconsistent numbering in BPSEQ format: previous=0, current=0 for example4, commented out for now
                        // Pair.of(BasePairAnalyzerEnum.RNAVIEW, context.getBean(BPNetBasePairAnalyzer.class))
                        // TODO: rnaview throws Inconsistent numbering in BPSEQ format: previous=0, current=0 for example4, commented out for now
                        // Pair.of(BasePairAnalyzerEnum.RNAVIEW, context.getBean(RnaViewBasePairAnalyzer.class))
                );

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

        final List<BpSeqInfo> bpSeqInfos = consensus.getLeft().getBpSeqInfos();
        final List<SVGDocument> svgDocuments = consensus.getRight().getSvgDocuments();
        final int size = svgDocuments.size();
        assert bpSeqInfos.size() == svgDocuments.size();

        List<OutputMultiEntry> outputMultiEntryList = IntStream
                .range(0, size)
                .mapToObj(i -> mapBpSeqInfoAndConsensusImageIntoOutputMultiEntry(
                        visualizationTool,
                        bpSeqInfos.get(i),
                        svgDocuments.get(i)))
                .collect(Collectors.toList());

        return new OutputMulti()
                .withEntries(outputMultiEntryList);
    }

    private OutputMultiEntry mapBpSeqInfoAndConsensusImageIntoOutputMultiEntry(VisualizationTool visualizationTool,
                                                                               BpSeqInfo bpSeqInfo,
                                                                               SVGDocument consensusImage) {
        // TODO: using findFirst, because in future implementation using MILP the bpSeqInfo will only contain 1 dotBracket
        //  object, refactor when rnapdbee-common code is merged with the engine's code
        final DotBracket dotBracket = bpSeqInfo.uniqueDotBrackets().keySet()
                .stream().findFirst()
                .orElseThrow(RuntimeException::new);
        final SecondaryStructureImage secondaryVisualization = imageService.provideVisualization(visualizationTool, dotBracket);

        final String svgConsensusVisualizationUrl = getSvgUrl(servletContext, consensusImage);
        final String pngConsensusVisualizationUrl = getPngUrl(servletContext, consensusImage);

        SingleSecondaryModelAnalysisOutput secondaryAnalysisOutput = new SingleSecondaryModelAnalysisOutput()
                .withBpSeq(analysisOutputsMapper.mapBpSeqToListOfString(bpSeqInfo.getBpSeq()))
                .withCt(analysisOutputsMapper.mapCtToListOfString(bpSeqInfo.getCt()))
                .withImageInformation(analysisOutputsMapper.mapSecondaryStructureImageIntoImageInformationOutput(secondaryVisualization));
        Output2D output2D = new Output2D()
                .withAnalysis(List.of(secondaryAnalysisOutput));
        ConsensusVisualization consensusVisualization =
                new ConsensusVisualization(pngConsensusVisualizationUrl, svgConsensusVisualizationUrl);

        return new OutputMultiEntry()
                .withOutput2D(output2D)
                .withConsensusVisualization(consensusVisualization);
    }

    private String getSvgUrl(ServletContext servletContext, SVGDocument image) {
        try {
            return ImageUtils.generateSvgUrl(servletContext, image).getRight();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPngUrl(ServletContext servletContext, SVGDocument image) {
        try {
            return ImageUtils.generatePngUrl(servletContext, image).getRight();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public ConsensusStructureAnalysisService(ImageService imageService,
                                             AnalysisOutputsMapper analysisOutputsMapper,
                                             ServletContext servletContext,
                                             MCAnnotateBasePairAnalyzer mcAnnotateBasePairAnalyzer) {
        this.imageService = imageService;
        this.analysisOutputsMapper = analysisOutputsMapper;
        this.servletContext = servletContext;
        this.mcAnnotateBasePairAnalyzer = mcAnnotateBasePairAnalyzer;
    }
}
