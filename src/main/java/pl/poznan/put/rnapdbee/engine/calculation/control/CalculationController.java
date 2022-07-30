package pl.poznan.put.rnapdbee.engine.calculation.control;


import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.rnapdbee.engine.calculation.logic.EncodingUtils;
import pl.poznan.put.rnapdbee.engine.calculation.logic.SecondaryStructureAnalysisService;
import pl.poznan.put.rnapdbee.engine.calculation.mapper.AnalysisOutputsMapper;
import pl.poznan.put.rnapdbee.engine.calculation.model.Output2D;
import pl.poznan.put.rnapdbee.engine.model.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.model.ModelSelection;
import pl.poznan.put.rnapdbee.engine.model.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.engine.model.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;


/**
 * Controller class for the Calculation API.
 */
@RestController
@RequestMapping("api/v1/calculation")
public class CalculationController {

    private final Logger logger;

    private final SecondaryStructureAnalysisService secondaryStructureAnalysisService;

    private final AnalysisOutputsMapper analysisOutputsMapper;

    @PostMapping(path = "/3d", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Object> calculateTertiaryToDotBracket(
            @RequestParam("modelSelection") ModelSelection modelSelection,
            @RequestParam("analysisTool") AnalysisTool analysisTool,
            @RequestParam("nonCanonicalHandling") NonCanonicalHandling nonCanonicalHandling,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestBody String content) {
        throw new UnsupportedOperationException();
    }


    @PostMapping(path = "/2d", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Output2D> calculateSecondaryToDotBracket(
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestHeader("Content-Disposition") String contentDispositionHeader,
            @RequestBody String encodedContent) {

        logger.info(String.format("Analysis of scenario 2D -> (...) started for content-disposition header %s",
                contentDispositionHeader));ContentDisposition contentDisposition = ContentDisposition.parse(contentDispositionHeader);
        String decodedContent = EncodingUtils.decodeBase64ToString(encodedContent);
        var analysisResult = secondaryStructureAnalysisService
                .analyseSecondaryStructureFile(
                        structuralElementsHandling,
                        visualizationTool,
                        removeIsolated,
                        decodedContent,
                        contentDisposition.getFilename());
        var outputAnalysis = analysisOutputsMapper.mapToOutput2D(analysisResult);
        return new ResponseEntity<>(outputAnalysis, HttpStatus.OK);
    }


    @PostMapping(path = "/multi", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Object> calculateTertiaryToMultiSecondary(
            @RequestParam("modelSelection") ModelSelection modelSelection,
            @RequestParam("includeNonCanonical") boolean includeNonCanonical,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestBody String content) {
        throw new UnsupportedOperationException();
    }


    /**
     * Endpoint responsible for (...) -> Image analysis.
     *
     * @param structuralElementsHandling enum determining if pseudoknots should be considered or not
     * @param visualizationTool          enum for Visualization Tool
     * @param encodedContent             base64 encoded content of the uploaded file
     * @return wrapped in an object list of image outputs
     */
    @PostMapping(path = "/image", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Output2D> calculateDotBracketToImage(
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestHeader("Content-Disposition") String contentDispositionHeader,
            @RequestBody String encodedContent) {

        logger.info(String.format("Analysis of scenario (...) -> Image started for content-disposition header %s",
                contentDispositionHeader));
        ContentDisposition contentDisposition = ContentDisposition.parse(contentDispositionHeader);
        String decodedContent = EncodingUtils.decodeBase64ToString(encodedContent);
        var analysisResult = secondaryStructureAnalysisService
                .analyseDotBracketNotationFile(
                        structuralElementsHandling,
                        visualizationTool,
                        decodedContent,
                        contentDisposition.getFilename());
        var outputAnalysis = analysisOutputsMapper.mapToOutput2D(analysisResult);
        return new ResponseEntity<>(outputAnalysis, HttpStatus.OK);
    }

    @Autowired
    private CalculationController(SecondaryStructureAnalysisService secondaryStructureAnalysisService,
                                  AnalysisOutputsMapper analysisOutputsMapper,
                                  Logger logger) {
        this.secondaryStructureAnalysisService = secondaryStructureAnalysisService;
        this.analysisOutputsMapper = analysisOutputsMapper;
        this.logger = logger;
    }
}
