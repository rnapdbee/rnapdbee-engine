package pl.poznan.put.rnapdbee.engine.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pl.poznan.put.rnapdbee.engine.model.OutputImage;
import pl.poznan.put.rnapdbee.engine.model.ThreeDotsToImagePayload;

@RestController
@RequestMapping("api/v1/engine/image")
public class EngineImageController {

    private static final Logger logger = LoggerFactory.getLogger(EngineImageController.class);

    @PostMapping(path = "/", produces = "application/json")
    public OutputImage calculateMulti(@RequestBody ThreeDotsToImagePayload threeDotsToImagePayload) {
        return new OutputImage();
    }
}