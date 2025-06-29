package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RnaPDBeeAdaptersCaller;

@Component
public class RChieStructureDrawer extends RnaPDBeeAdaptersStructureDrawer {

    @Override
    public VisualizationTool getEnum() {
        return VisualizationTool.R_CHIE;
    }

    @Autowired
    public RChieStructureDrawer(RnaPDBeeAdaptersCaller rnaPDBeeAdaptersCaller) {
        super(rnaPDBeeAdaptersCaller);
    }
}
