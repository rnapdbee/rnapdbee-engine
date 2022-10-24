package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;

import jdk.jfr.Experimental;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Class that's purpose is to communicate with rnapdbee-adapters for analysis on FR3D base pair analyzer.
 * The FR3D-python program, used in rnapdbee-adapters is not yet production-ready, thus it is annotated as experimental.
 */
@Component
@Experimental
public class Fr3dBasePairAnalyzer extends BasePairAnalyzer {

    @Autowired
    public Fr3dBasePairAnalyzer(@Value("${rnapdbee.adapters.global.fr3d.path}") String pathToMCAnnotateAdapter,
                                @Autowired @Qualifier("adaptersWebClient") WebClient adaptersWebClient) {
        super(adaptersWebClient);
        this.adapterURI = pathToMCAnnotateAdapter;
    }
}
