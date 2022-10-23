package pl.poznan.put.rnapdbee.engine.calculation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.ConsensualStructureAnalysisService;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.SecondaryStructureAnalysisService;
import pl.poznan.put.rnapdbee.engine.calculation.tertiary.TertiaryStructureAnalysisService;
import pl.poznan.put.rnapdbee.engine.shared.map.AnalysisOutputsMapper;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.Output2D;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.ModelSelection;
import pl.poznan.put.rnapdbee.engine.shared.domain.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.engine.calculation.tertiary.domain.Output3D;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMulti;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementsHandling;

/**
 * Middleware between CalculationController and Structure Analysis Services.
 */
@Component
public class CalculationService {

    private final SecondaryStructureAnalysisService secondaryStructureAnalysisService;
    private final ConsensualStructureAnalysisService consensualStructureAnalysisService;
    private final TertiaryStructureAnalysisService tertiaryStructureAnalysisService;

    public Output2D handleSecondaryToDotBracketCalculation(StructuralElementsHandling structuralElementsHandling,
                                                           VisualizationTool visualizationTool,
                                                           boolean removeIsolated,
                                                           String content,
                                                           String filename) {

        return secondaryStructureAnalysisService
                .analyseSecondaryStructureFile(
                        structuralElementsHandling,
                        visualizationTool,
                        removeIsolated,
                        content,
                        filename);
    }

    public OutputMulti handleTertiaryToMultiSecondaryCalculation(ModelSelection modelSelection,
                                                                 boolean includeNonCanonical,
                                                                 boolean removeIsolated,
                                                                 VisualizationTool visualizationTool,
                                                                 String filename,
                                                                 String content) {
        return null;
//        return consensualStructureAnalysisService
//                .analyse(modelSelection,
//                        includeNonCanonical,
//                        removeIsolated,
//                        visualizationTool,
//                        filename,
//                        content);
    }

    public Output3D handleTertiaryToDotBracketCalculation(ModelSelection modelSelection,
                                                          AnalysisTool analysisTool,
                                                          NonCanonicalHandling nonCanonicalHandling,
                                                          boolean removeIsolated,
                                                          StructuralElementsHandling structuralElementsHandling,
                                                          VisualizationTool visualizationTool,
                                                          String filename,
                                                          String fileContent) {
        return null;

//        return tertiaryStructureAnalysisService
//                .analyse(modelSelection,
//                        analysisTool,
//                        nonCanonicalHandling,
//                        removeIsolated,
//                        structuralElementsHandling,
//                        visualizationTool,
//                        filename,
//                        fileContent);
    }

    @Autowired
    private CalculationService(SecondaryStructureAnalysisService secondaryStructureAnalysisService,
                               ConsensualStructureAnalysisService consensualStructureAnalysisService,
                               TertiaryStructureAnalysisService tertiaryStructureAnalysisService) {
        this.secondaryStructureAnalysisService = secondaryStructureAnalysisService;
        this.consensualStructureAnalysisService = consensualStructureAnalysisService;
        this.tertiaryStructureAnalysisService = tertiaryStructureAnalysisService;
    }
}
