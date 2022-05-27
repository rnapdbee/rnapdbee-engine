package pl.poznan.put.rnapdbee.engine.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pl.poznan.put.rnapdbee.engine.model.Output2D;
import pl.poznan.put.rnapdbee.engine.model.Payload2DToThreeDots;

@RestController
@RequestMapping("api/rnapdbee/engine/2d")
public class Engine2DController {

    private static final Logger logger = LoggerFactory.getLogger(Engine2DController.class);

    @PostMapping(path = "/", produces = "application/json")
    public Output2D calculate2D(@RequestBody Payload2DToThreeDots payload2DToThreeDots) {
        return new Output2D();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public Output2D fetch2D(@PathVariable(name = "id") Integer id) {
        return new Output2D();
    }
}