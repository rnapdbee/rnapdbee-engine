package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RnaPDBeeAdaptersCaller;
import pl.poznan.put.rnapdbee.engine.shared.basepair.exception.AdaptersErrorException;


@Component
public class MCAnnotateBasePairAnalyzer extends BasePairAnalyzer {

    @Override
    public AnalysisTool analysisTool() {
        return AnalysisTool.MC_ANNOTATE;
    }

    @Override
    @Cacheable("AnalysisMCAnnotate")
    public BasePairAnalysis analyze(String fileContent, boolean includeNonCanonical, PdbModel structureModel)
            throws AdaptersErrorException {
        return super.performAnalysis(fileContent, includeNonCanonical, structureModel);
    }

    @Autowired
    public MCAnnotateBasePairAnalyzer(RnaPDBeeAdaptersCaller rnaPDBeeAdaptersCaller) {
        super(rnaPDBeeAdaptersCaller);
    }
}
