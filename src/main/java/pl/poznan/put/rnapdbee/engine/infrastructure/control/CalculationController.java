package pl.poznan.put.rnapdbee.engine.infrastructure.control;


import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.rnapdbee.engine.calculation.CalculationService;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.Output2D;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.ModelSelection;
import pl.poznan.put.rnapdbee.engine.shared.domain.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.engine.calculation.tertiary.domain.Output3D;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.engine.shared.parser.ContentDispositionParser;


/**
 * Controller class for the Calculation API.
 */
@RestController
@RequestMapping("calculation-api/v1/")
public class CalculationController {

    private final Logger logger = LoggerFactory.getLogger(CalculationController.class);

    private final ContentDispositionParser contentDispositionParser;
    private final CalculationService calculationService;

    @Autowired
    private CalculationController(CalculationService calculationService,
                                  ContentDispositionParser contentDispositionParser) {
        this.calculationService = calculationService;
        this.contentDispositionParser = contentDispositionParser;
    }

    /**
     * Endpoint responsible for 3D -> (....) analysis.
     *
     * @param modelSelection             enum defining whether calculation should be made on first or all models in file
     * @param analysisTool               analysis tool (adapter) used in determination of base pairs
     * @param nonCanonicalHandling       enum defining handling of non-canonical pairs
     * @param removeIsolated             boolean flag indicating whether isolated pairs should be removed or not
     * @param structuralElementsHandling enum defining handling of pseudoknots
     * @param visualizationTool          visualization tool used when creating the image
     * @param contentDispositionHeader   header containing name of the analyzed file
     * @param fileContent                content of the file
     * @return output3D
     */
    @Operation(summary = "Perform a 3D to Dot-Bracket calculation")
    @PostMapping(path = "/3d", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Output3D> calculateTertiaryToDotBracket(
            @RequestParam("modelSelection") ModelSelection modelSelection,
            @RequestParam("analysisTool") AnalysisTool analysisTool,
            @RequestParam("nonCanonicalHandling") NonCanonicalHandling nonCanonicalHandling,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestHeader(HttpHeaders.CONTENT_DISPOSITION) String contentDispositionHeader,
            @RequestBody String fileContent) {
        logger.info("Analysis of scenario 3D -> (...) started.");
        String filename = contentDispositionParser.parseContentDispositionHeader(contentDispositionHeader);
        var result = calculationService
                .handleTertiaryToDotBracketCalculation(
                        modelSelection,
                        analysisTool,
                        nonCanonicalHandling,
                        removeIsolated,
                        structuralElementsHandling,
                        visualizationTool,
                        filename,
                        fileContent);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Endpoint responsible for 2D -> (....) analysis.
     *
     * @param structuralElementsHandling enum determining if pseudoknots should be considered or not
     * @param visualizationTool          enum for Visualization Tool
     * @param removeIsolated             boolean value indicating if the isolated residues should be removed or not
     * @param contentDispositionHeader   Content-Disposition header containing name of the file
     * @param fileContent                content of the analyzed file
     * @return wrapped in an object list of image outputs
     */
    @Operation(summary = "Perform a 2D to Dot-Bracket calculation")
    @PostMapping(path = "/2d", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Output2D> calculateSecondaryToDotBracket(
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestHeader(HttpHeaders.CONTENT_DISPOSITION) String contentDispositionHeader,
            @RequestBody String fileContent) {

        logger.info("Analysis of scenario 2D -> (...) started.");
        String filename = contentDispositionParser.parseContentDispositionHeader(contentDispositionHeader);
        var outputAnalysis = calculationService
                .handleSecondaryToDotBracketCalculation(
                        structuralElementsHandling,
                        visualizationTool,
                        removeIsolated,
                        fileContent,
                        filename);
        return new ResponseEntity<>(outputAnalysis, HttpStatus.OK);
    }

    /**
     * Endpoint responsible for 3D -> multi 2D analysis.
     *
     * @param modelSelection           enum indicating if first, or all models from file should be analyzed
     * @param includeNonCanonical      boolean flag indicating if non-canonical pairs should be kept in analysis
     * @param removeIsolated           boolean flag indicating if isolated pairs should be removed from analysis
     * @param visualizationTool        enum indicating the tool/method that should be used in visualization
     * @param contentDispositionHeader Content-Disposition header containing name of the file
     * @param fileContent              content of the analyzed file
     * @return object with analysis output
     */
    @Operation(summary = "Perform a 3D to multi 2D calculation")
    @PostMapping(path = "/multi", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Object> calculateTertiaryToMultiSecondary(
            @RequestParam(value = "modelSelection", defaultValue = "FIRST") ModelSelection modelSelection,
            @RequestParam("includeNonCanonical") boolean includeNonCanonical,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestHeader(HttpHeaders.CONTENT_DISPOSITION) String contentDispositionHeader,
            @RequestBody String fileContent) {
        logger.info("Analysis of scenario 3D -> multi 2D started");
        String filename = contentDispositionParser.parseContentDispositionHeader(contentDispositionHeader);
        var result = calculationService.handleTertiaryToMultiSecondaryCalculation(modelSelection,
                includeNonCanonical, removeIsolated, visualizationTool, filename, fileContent);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
