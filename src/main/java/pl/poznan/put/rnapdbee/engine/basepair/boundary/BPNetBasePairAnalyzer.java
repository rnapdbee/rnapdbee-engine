package pl.poznan.put.rnapdbee.engine.basepair.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BPNetBasePairAnalyzer extends RNApdbeeAdapterBasePairAnalyzer {

    @Autowired
    public BPNetBasePairAnalyzer(@Value("${rnapdbee.adapters.global.bpnet.path}") String pathToMCAnnotateAdapter,
                                 @Autowired @Qualifier("adaptersWebClient") WebClient adaptersWebClient) {
        super(adaptersWebClient);
        this.adapterURI = pathToMCAnnotateAdapter;
    }
}
