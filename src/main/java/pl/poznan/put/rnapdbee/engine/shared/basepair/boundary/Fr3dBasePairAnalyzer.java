package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;

import jdk.jfr.Experimental;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RnaPDBeeAdaptersCaller;
import pl.poznan.put.rnapdbee.engine.shared.exception.AdaptersErrorException;

/**
 * Class that's purpose is to communicate with rnapdbee-adapters for analysis on FR3D base pair analyzer.
 * The FR3D-python program, used in rnapdbee-adapters is not yet production-ready, thus it is annotated as experimental.
 */
@Component
@Experimental
public class Fr3dBasePairAnalyzer extends BasePairAnalyzer {

    @Override
    public AnalysisTool analysisTool() {
        return AnalysisTool.FR3D_PYTHON;
    }

    @Override
    @Cacheable("AnalysisFr3d")
    public BasePairAnalysis analyze(String fileContent, boolean includeNonCanonical, int modelNumber)
            throws AdaptersErrorException {
        return super.performAnalysis(fileContent, includeNonCanonical, modelNumber);
    }

    @Autowired
    public Fr3dBasePairAnalyzer(RnaPDBeeAdaptersCaller rnaPDBeeAdaptersCaller) {
        super(rnaPDBeeAdaptersCaller);
    }
}
