package pl.poznan.put.rnapdbee.engine.calculation.control;


import io.swagger.v3.oas.annotations.Operation;
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
import pl.poznan.put.rnapdbee.engine.calculation.logic.CalculationService;
import pl.poznan.put.rnapdbee.engine.calculation.model.Output2D;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.model.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.model.ModelSelection;
import pl.poznan.put.rnapdbee.engine.model.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.engine.model.StructuralElementsHandling;


/**
 * Controller class for the Calculation API.
 */
@RestController
@RequestMapping("api/v1/calculation")
public class CalculationController {

    private final Logger logger;

    private final CalculationService calculationService;

    @Autowired
    private CalculationController(CalculationService calculationService, Logger logger) {
        this.calculationService = calculationService;
        this.logger = logger;
    }

    @Operation(summary = "Perform a 3D to Dot-Bracket calculation")
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

    /**
     * Endpoint responsible for 2D -> (....) analysis.
     *
     * @param structuralElementsHandling enum determining if pseudoknots should be considered or not
     * @param visualizationTool          enum for Visualization Tool
     * @param removeIsolated             boolean value indicating if the isolated residues should be removed or not
     * @param contentDispositionHeader   Content-Disposition header containing name of the file
     * @param fileContent                content of the analysed file
     * @return wrapped in an object list of image outputs
     */
    @Operation(summary = "Perform a 2D to Dot-Bracket calculation")
    @PostMapping(path = "/2d", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Output2D> calculateSecondaryToDotBracket(
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestHeader("Content-Disposition") String contentDispositionHeader,
            @RequestBody String fileContent) {

        logger.info(String.format("Analysis of scenario 2D -> (...) started for content-disposition header %s",
                contentDispositionHeader));
        ContentDisposition contentDisposition = ContentDisposition.parse(contentDispositionHeader);
        var outputAnalysis = calculationService
                .handleSecondaryToDotBracketCalculation(
                        structuralElementsHandling,
                        visualizationTool,
                        removeIsolated,
                        fileContent,
                        contentDisposition.getFilename());
        return new ResponseEntity<>(outputAnalysis, HttpStatus.OK);
    }

    /**
     * Endpoint responsible for 3D -> multi 2D analysis.
     * @param modelSelection            enum indicating if first, or all models from file should be analysed
     * @param includeNonCanonical       boolean flag indicating if non-canonical pairs should be kept in analysis
     * @param removeIsolated            boolean flag indicating if isolated pairs should be removed from analysis
     * @param visualizationTool         enum indicating the tool/method that should be used in visualization
     * @param contentDispositionHeader  Content-Disposition header containing name of the file
     * @param fileContent               content of the analysed file
     * @return object with analysis output
     */
    @Operation(summary = "Perform a 3D to multi 2D calculation")
    @PostMapping(path = "/multi", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Object> calculateTertiaryToMultiSecondary(
            @RequestParam("modelSelection") ModelSelection modelSelection,
            @RequestParam("includeNonCanonical") boolean includeNonCanonical,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestHeader("Content-Disposition") String contentDispositionHeader,
            @RequestBody String fileContent) {
        logger.info(String.format("Analysis of scenario 3D -> multi 2D started for content-disposition header %s",
                contentDispositionHeader));
        ContentDisposition contentDisposition = ContentDisposition.parse(contentDispositionHeader);
        var result = calculationService.handleTertiaryToMultiSecondaryCalculation(modelSelection,
                includeNonCanonical, removeIsolated, visualizationTool, contentDisposition.getFilename(), fileContent);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
