package pl.poznan.put.rnapdbee.engine.calculation.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.poznan.put.rnapdbee.engine.calculation.mapper.AnalysisOutputsMapper;
import pl.poznan.put.rnapdbee.engine.calculation.model.Output2D;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.model.StructuralElementsHandling;

@Component
public class CalculationService {

    private final SecondaryStructureAnalysisService secondaryStructureAnalysisService;

    private final AnalysisOutputsMapper analysisOutputsMapper;

    public Output2D handleDotBracketToImageCalculation(
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String content,
            String filename) {

        var analysisResult =
                secondaryStructureAnalysisService.analyseDotBracketNotationFile(
                        structuralElementsHandling, visualizationTool, content, filename);

        return analysisOutputsMapper.mapToOutput2D(analysisResult);
    }

    public Output2D handleSecondaryToDotBracketCalculation(
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            boolean removeIsolated,
            String content,
            String filename) {

        var analysisResult =
                secondaryStructureAnalysisService.analyseSecondaryStructureFile(
                        structuralElementsHandling,
                        visualizationTool,
                        removeIsolated,
                        content,
                        filename);
        return analysisOutputsMapper.mapToOutput2D(analysisResult);
    }

    @Autowired
    private CalculationService(
            SecondaryStructureAnalysisService secondaryStructureAnalysisService,
            AnalysisOutputsMapper analysisOutputsMapper) {
        this.secondaryStructureAnalysisService = secondaryStructureAnalysisService;
        this.analysisOutputsMapper = analysisOutputsMapper;
    }
}
