package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;

@Component
public class RnaViewBasePairAnalyzer extends BasePairAnalyzer {

    @Override
    @Cacheable("AnalysisRnaView")
    public BasePairAnalysis analyze(String fileContent, boolean includeNonCanonical, int modelNumber) {
        return super.performAnalysis(fileContent, includeNonCanonical, modelNumber);
    }

    @Autowired
    public RnaViewBasePairAnalyzer(@Value("${rnapdbee.adapters.global.rnaview.path}") String pathToRnaViewAdapter,
                                   @Autowired @Qualifier("adaptersWebClient") WebClient adaptersWebClient) {
        super(adaptersWebClient, pathToRnaViewAdapter);
    }
}
