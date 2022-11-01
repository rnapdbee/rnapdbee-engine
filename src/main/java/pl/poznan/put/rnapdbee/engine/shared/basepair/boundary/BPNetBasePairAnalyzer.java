package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;

@Component
public class BPNetBasePairAnalyzer extends BasePairAnalyzer {

    @Override
    @Cacheable("AnalysisBPNet")
    public BasePairAnalysis analyze(String fileContent, boolean includeNonCanonical, int modelNumber) {
        return super.performAnalysis(fileContent, includeNonCanonical, modelNumber);
    }

    @Autowired
    public BPNetBasePairAnalyzer(@Value("${rnapdbee.adapters.global.bpnet.path}") String pathToMCAnnotateAdapter,
                                 @Autowired @Qualifier("adaptersWebClient") WebClient adaptersWebClient) {
        super(adaptersWebClient, pathToMCAnnotateAdapter);
    }
}
