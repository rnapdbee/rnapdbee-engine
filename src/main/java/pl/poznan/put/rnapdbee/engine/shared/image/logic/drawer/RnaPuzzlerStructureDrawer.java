package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RNApdbeeAdaptersCaller;

@Component
public class RnaPuzzlerStructureDrawer extends RnaPDBeeAdaptersStructureDrawer {

    @Override
    public VisualizationTool getEnum() {
        return VisualizationTool.RNA_PUZZLER;
    }

    @Autowired
    public RnaPuzzlerStructureDrawer(RNApdbeeAdaptersCaller rnApdbeeAdaptersCaller) {
        super(rnApdbeeAdaptersCaller);
    }
}
