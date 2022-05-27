package pl.poznan.put.rnapdbee.engine.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pl.poznan.put.rnapdbee.engine.model.Output3D;
import pl.poznan.put.rnapdbee.engine.model.Payload3DThreeDots;

@RestController
@RequestMapping("api/rnapdbee/engine/3d")
public class Engine3DController {

  private static final Logger logger = LoggerFactory.getLogger(Engine3DController.class);

  @PostMapping(path = "/", produces = "application/json")
  public Output3D calculate3D(@RequestBody Payload3DThreeDots payload3DThreeDots) {
    return new Output3D();
  }

  @GetMapping(path = "/{id}", produces = "application/json")
  public Output3D getExisting3D(@PathVariable(name = "id") Integer id) {
    return new Output3D();
  }

  @PostMapping(path = "/pdb/{pdbId}", produces = "application/json")
  public Output3D calculatePDB3D(@PathVariable(name = "pdbId") String pdbId) {
    return new Output3D();
  }
}
