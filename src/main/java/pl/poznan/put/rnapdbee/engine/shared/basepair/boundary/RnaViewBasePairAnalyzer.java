package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RnaPDBeeAdaptersCaller;

@Component
public class RnaViewBasePairAnalyzer extends BasePairAnalyzer {

    @Override
    public AnalysisTool analysisTool() {
        return AnalysisTool.RNAVIEW;
    }

    @Override
    @Cacheable("AnalysisRnaView")
    public BasePairAnalysis analyze(String fileContent, boolean includeNonCanonical, int modelNumber) {
        return super.performAnalysis(fileContent, includeNonCanonical, modelNumber);
    }

    @Autowired
    public RnaViewBasePairAnalyzer(RnaPDBeeAdaptersCaller rnaPDBeeAdaptersCaller) {
        super(rnaPDBeeAdaptersCaller);
    }
}
