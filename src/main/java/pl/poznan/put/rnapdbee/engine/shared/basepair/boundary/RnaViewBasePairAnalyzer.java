package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RnaViewBasePairAnalyzer extends BasePairAnalyzer {

    @Autowired
    public RnaViewBasePairAnalyzer(@Value("${rnapdbee.adapters.global.rnaview.path}") String pathToRnaViewAdapter,
                                   @Autowired @Qualifier("adaptersWebClient") WebClient adaptersWebClient) {
        super(adaptersWebClient);
        this.adapterURI = pathToRnaViewAdapter;
    }
}
