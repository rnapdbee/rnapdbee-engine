package pl.poznan.put.rnapdbee.engine.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.visualization.boundary.ConsensualVisualizationDrawer;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.visualization.boundary.WeblogoConsensualVisualizationDrawer;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RnaPDBeeAdaptersCaller;

public class ConsensualVisualizationDrawerConfiguration {

    @Bean
    ConsensualVisualizationDrawer consensualVisualizationDrawer(RnaPDBeeAdaptersCaller rnaPDBeeAdaptersCaller) {
        return new WeblogoConsensualVisualizationDrawer(rnaPDBeeAdaptersCaller);
    }
}
