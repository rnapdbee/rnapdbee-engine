package pl.poznan.put.rnapdbee.engine.calculation.consensus.visualization.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RNApdbeeAdaptersCaller;

import java.util.List;

@Service
public class WeblogoConsensualVisualizationDrawer implements ConsensualVisualizationDrawer {

    private final RNApdbeeAdaptersCaller rnapdbeeAdaptersCaller;

    @Override
    public byte[] performVisualization(List<OutputMultiEntry> outputMultiEntries) {
        return rnapdbeeAdaptersCaller.performWeblogoVisualization(outputMultiEntries);
    }

    @Autowired
    public WeblogoConsensualVisualizationDrawer(RNApdbeeAdaptersCaller rnapdbeeAdaptersCaller) {
        this.rnapdbeeAdaptersCaller = rnapdbeeAdaptersCaller;
    }
}
