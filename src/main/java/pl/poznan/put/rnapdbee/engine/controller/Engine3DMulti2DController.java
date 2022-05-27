package pl.poznan.put.rnapdbee.engine.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pl.poznan.put.rnapdbee.engine.model.OutputMulti;
import pl.poznan.put.rnapdbee.engine.model.Payload3DToMulti2D;

@RestController
@RequestMapping("api/rnapdbee/engine/multi")
public class Engine3DMulti2DController {

    private static final Logger logger = LoggerFactory.getLogger(Engine3DMulti2DController.class);

    @PostMapping(path = "/", produces = "application/json")
    public OutputMulti calculateMulti(@RequestBody Payload3DToMulti2D payload3DToMulti2D) {
        return new OutputMulti();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public OutputMulti fetchExistingMulti(@PathVariable(name = "id") Integer id) {
        return new OutputMulti();
    }

    @PostMapping(path = "/pdb/{pdbId}", produces = "application/json")
    public OutputMulti calculatePDBMulti(@PathVariable(name = "pdbId") String pdbId) {
        return new OutputMulti();
    }
}
