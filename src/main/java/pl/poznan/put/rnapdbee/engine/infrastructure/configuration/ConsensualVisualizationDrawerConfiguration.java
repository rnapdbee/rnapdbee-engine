package pl.poznan.put.rnapdbee.engine.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.ConsensualVisualizationDrawer;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.WeblogoConsensualVisualizationDrawer;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RnaPDBeeAdaptersCaller;

public class ConsensualVisualizationDrawerConfiguration {

    @Bean
    ConsensualVisualizationDrawer consensualVisualizationDrawer(RnaPDBeeAdaptersCaller rnaPDBeeAdaptersCaller) {
        return new WeblogoConsensualVisualizationDrawer(rnaPDBeeAdaptersCaller);
    }
}
