package pl.poznan.put.rnapdbee.engine.calculation.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.rnapdbee.engine.model.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.model.ModelSelection;
import pl.poznan.put.rnapdbee.engine.model.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.engine.model.Output2D;
import pl.poznan.put.rnapdbee.engine.model.Output3D;
import pl.poznan.put.rnapdbee.engine.model.OutputImage;
import pl.poznan.put.rnapdbee.engine.model.OutputMulti;
import pl.poznan.put.rnapdbee.engine.model.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.engine.model.VisualizationTool;

@RestController
@RequestMapping("api/v1/calculation")
public class CalculationController
{



    private static final Logger logger = LoggerFactory.getLogger(CalculationController.class);


    @PostMapping(path = "/3d", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Output3D> calculate3dToTertiary(
            @RequestParam("modelSelection") ModelSelection modelSelection,
            @RequestParam("analysisTool") AnalysisTool analysisTool,
            @RequestParam("nonCanonicalHandling") NonCanonicalHandling nonCanonicalHandling,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestBody String content)
    {return new ResponseEntity<>(new Output3D(), HttpStatus.OK);
    }


    @PostMapping(path = "/2d", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<Output2D> calculate2dToTertiary(
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool, @RequestBody String content) {
        return new ResponseEntity<>(new Output2D(), HttpStatus.OK);
    }


    @PostMapping(path = "/multi", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<OutputMulti> calculate2dToMulti2d(
            @RequestParam("modelSelection") ModelSelection modelSelection,
            @RequestParam("includeNonCanonical") boolean includeNonCanonical,
            @RequestParam("removeIsolated") boolean removeIsolated,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestBody String content) {
        return new ResponseEntity<>(new OutputMulti(), HttpStatus.OK);
    }


    @PostMapping(path = "/image", produces = "application/json", consumes = "text/plain")
    public ResponseEntity<OutputImage>
    calculateTertiaryToImage
            (
            @RequestParam("structuralElementsHandling") StructuralElementsHandling structuralElementsHandling,
            @RequestParam("visualizationTool") VisualizationTool visualizationTool,
            @RequestBody String content) {
        return new ResponseEntity<>(new OutputImage(), HttpStatus.OK);
    }
}