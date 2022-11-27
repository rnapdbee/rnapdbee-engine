package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.engine.infrastructure.configuration.RnapdbeeAdaptersProperties;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;

@Component
public class RnapolisBasePairAnalyzer extends BasePairAnalyzer {

    @Override
    @Cacheable("AnalysisRNAPolis")
    public BasePairAnalysis analyze(String fileContent, boolean includeNonCanonical, int modelNumber) {
        return super.performAnalysis(fileContent, includeNonCanonical, modelNumber);
    }

    @Autowired
    public RnapolisBasePairAnalyzer(RnapdbeeAdaptersProperties properties,
                                    @Autowired @Qualifier("adaptersWebClient") WebClient adaptersWebClient) {
        super(properties, adaptersWebClient, properties.getRnapolisPath());
    }
}
