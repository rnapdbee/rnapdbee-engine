package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class MCAnnotateBasePairAnalyzer extends BasePairAnalyzer {

    @Autowired
    public MCAnnotateBasePairAnalyzer(@Value("${rnapdbee.adapters.global.mcannotate.path}") String pathToMCAnnotateAdapter,
                                      @Autowired @Qualifier("adaptersWebClient") WebClient adaptersWebClient) {
        super(adaptersWebClient);
        this.adapterURI = pathToMCAnnotateAdapter;
    }
}
