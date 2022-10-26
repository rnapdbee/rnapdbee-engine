package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BPNetBasePairAnalyzer extends BasePairAnalyzer {

    @Autowired
    public BPNetBasePairAnalyzer(@Value("${rnapdbee.adapters.global.bpnet.path}") String pathToMCAnnotateAdapter,
                                 @Autowired @Qualifier("adaptersWebClient") WebClient adaptersWebClient) {
        super(adaptersWebClient);
        this.adapterURI = pathToMCAnnotateAdapter;
    }
}
