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
public class RnaViewBasePairAnalyzer extends RNApdbeeAdapterBasePairAnalyzer {

    @Autowired
    public RnaViewBasePairAnalyzer(@Value("${rnapdbee.adapters.global.rnaview.path}") String pathToRnaViewAdapter,
                                   @Autowired @Qualifier("adaptersWebClient") WebClient adaptersWebClient) {
        super(adaptersWebClient);
        this.adapterURI = pathToRnaViewAdapter;
    }
}
