package pl.poznan.put.rnapdbee.engine.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.visualization.boundary.ConsensualVisualizationDrawer;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.visualization.boundary.WeblogoConsensualVisualizationDrawer;

public class ConsensualVisualizationDrawerConfiguration {

    @Bean
    ConsensualVisualizationDrawer consensualVisualizationDrawer(
            @Autowired @Qualifier("adaptersWebClient") WebClient adaptersWebClient,
            @Value("${rnapdbee.adapters.global.weblogo.path}") String pathToWeblogoEndpoint) {
        return new WeblogoConsensualVisualizationDrawer(pathToWeblogoEndpoint, adaptersWebClient);
    }
}
