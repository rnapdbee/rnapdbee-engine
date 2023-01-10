package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.engine.shared.image.exception.VisualizationException;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RnaPDBeeAdaptersCaller;

import java.util.List;

@Service
public class WeblogoConsensualVisualizationDrawer implements ConsensualVisualizationDrawer {

    private final RnaPDBeeAdaptersCaller rnapdbeeAdaptersCaller;

    @Override
    public byte[] performVisualization(List<OutputMultiEntry> outputMultiEntries) throws VisualizationException {
        return rnapdbeeAdaptersCaller.performWeblogoVisualization(outputMultiEntries);
    }

    @Autowired
    public WeblogoConsensualVisualizationDrawer(RnaPDBeeAdaptersCaller rnapdbeeAdaptersCaller) {
        this.rnapdbeeAdaptersCaller = rnapdbeeAdaptersCaller;
    }
}
