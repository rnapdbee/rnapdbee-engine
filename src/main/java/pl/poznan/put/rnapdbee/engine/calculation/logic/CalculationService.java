package pl.poznan.put.rnapdbee.engine.calculation.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.calculation.mapper.AnalysisOutputsMapper;
import pl.poznan.put.rnapdbee.engine.calculation.model.Output2D;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.model.ModelSelection;
import pl.poznan.put.rnapdbee.engine.model.OutputMulti;
import pl.poznan.put.rnapdbee.engine.model.StructuralElementsHandling;

/**
 * Middleware between CalculationController and Structure Analysis Services.
 */
@Component
public class CalculationService {

    private final SecondaryStructureAnalysisService secondaryStructureAnalysisService;

    private final AnalysisOutputsMapper analysisOutputsMapper;

    private final ConsensusStructureAnalysisService consensusStructureAnalysisService;

    public Output2D handleSecondaryToDotBracketCalculation(StructuralElementsHandling structuralElementsHandling,
                                                           VisualizationTool visualizationTool,
                                                           boolean removeIsolated,
                                                           String content,
                                                           String filename) {

        var analysisResult = secondaryStructureAnalysisService
                .analyseSecondaryStructureFile(
                        structuralElementsHandling,
                        visualizationTool,
                        removeIsolated,
                        content,
                        filename);
        return analysisOutputsMapper.mapToOutput2D(analysisResult);
    }

    public OutputMulti handleTertiaryToMultiSecondaryCalculation(ModelSelection modelSelection,
                                                                 boolean includeNonCanonical,
                                                                 boolean removeIsolated,
                                                                 VisualizationTool visualizationTool,
                                                                 String filename,
                                                                 String content) {
        return consensusStructureAnalysisService
                .analyse(modelSelection,
                        includeNonCanonical,
                        removeIsolated,
                        visualizationTool,
                        filename,
                        content);
    }

    @Autowired
    private CalculationService(SecondaryStructureAnalysisService secondaryStructureAnalysisService,
                               AnalysisOutputsMapper analysisOutputsMapper,
                               ConsensusStructureAnalysisService consensusStructureAnalysisService) {
        this.secondaryStructureAnalysisService = secondaryStructureAnalysisService;
        this.analysisOutputsMapper = analysisOutputsMapper;
        this.consensusStructureAnalysisService = consensusStructureAnalysisService;
    }
}
