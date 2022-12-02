package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RnaPDBeeAdaptersCaller;
import pl.poznan.put.rnapdbee.engine.shared.exception.AdaptersErrorException;

@Component
public class BarnabaBasePairAnalyzer extends BasePairAnalyzer {

    @Override
    public AnalysisTool analysisTool() {
        return AnalysisTool.BARNABA;
    }

    @Override
    @Cacheable("AnalysisBarnaba")
    public BasePairAnalysis analyze(String fileContent, boolean includeNonCanonical, int modelNumber)
            throws AdaptersErrorException {
        return super.performAnalysis(fileContent, includeNonCanonical, modelNumber);
    }

    @Autowired
    public BarnabaBasePairAnalyzer(RnaPDBeeAdaptersCaller rnaPDBeeAdaptersCaller) {
        super(rnaPDBeeAdaptersCaller);
    }
}

