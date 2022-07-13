package pl.poznan.put.rnapdbee.engine.calculation.control;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.rnapdbee.engine.calculation.logic.EncodingUtils;
import pl.poznan.put.rnapdbee.engine.calculation.logic.TertiaryToImageService;
import pl.poznan.put.rnapdbee.engine.calculation.model.TertiaryAnalysisOutput;
import pl.poznan.put.rnapdbee.engine.model.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.model.ModelSelection;
import pl.poznan.put.rnapdbee.engine.model.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.engine.model.Output2D;
import pl.poznan.put.rnapdbee.engine.model.Output3D;
import pl.poznan.put.rnapdbee.engine.model.OutputMulti;
import pl.poznan.put.rnapdbee.engine.model.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;


/**
 * Controller class for the Calculation API.
 */
@RestController
@RequestMapping("api/v1/calculation")
public class CalculationController {

    // TODO make Autowired
    private static final Logger logger = LoggerFactory.getLogger(CalculationController.class);

    // TODO eventually put Autowired in constructor
    @Autowired
    private TertiaryToImageService tertiaryToImageService;

    @PostMapping(path = "/3d", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Output3D> calculate3dToTertiary(
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
    public ResponseEntity<Output2D> calculate2dToTertiary(
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestBody String content) {
        throw new UnsupportedOperationException();
    }


    @PostMapping(path = "/multi", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<OutputMulti> calculate2dToMulti2d(
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
    // TODO add content-disposition
    @PostMapping(path = "/image", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<TertiaryAnalysisOutput> calculateTertiaryToImage(
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestBody String encodedContent) {

        String decodedContent = EncodingUtils.decodeBase64StringToString(encodedContent);
        var result = tertiaryToImageService
                .performTertiaryToImageCalculation(structuralElementsHandling, visualizationTool, decodedContent);
        // TODO add ImageURL to the TertiaryAnalysisOutput
        return new ResponseEntity<>(new TertiaryAnalysisOutput(result), HttpStatus.OK);

    }
}
