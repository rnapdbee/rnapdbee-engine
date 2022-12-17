package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RNApdbeeAdaptersCaller;

@Component
public class PseudoViewerStructureDrawer extends RnaPDBeeAdaptersStructureDrawer {

    @Override
    public VisualizationTool getEnum() {
        return VisualizationTool.PSEUDO_VIEWER;
    }

    @Autowired
    public PseudoViewerStructureDrawer(RNApdbeeAdaptersCaller rnApdbeeAdaptersCaller) {
        super(rnApdbeeAdaptersCaller);
    }
}
