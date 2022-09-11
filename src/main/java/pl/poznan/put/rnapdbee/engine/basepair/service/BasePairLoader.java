package pl.poznan.put.rnapdbee.engine.basepair.service;

import edu.put.rnapdbee.analysis.BasePairAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.basepair.boundary.BPNetBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.basepair.boundary.BarnabaBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.basepair.boundary.Fr3dBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.basepair.boundary.MCAnnotateBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.basepair.boundary.RnaViewBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.model.AnalysisTool;

@Component
public class BasePairLoader {

    private final RnaViewBasePairAnalyzer rnaViewBasePairAnalyzer;
    private final MCAnnotateBasePairAnalyzer mcAnnotateBasePairAnalyzer;
    private final Fr3dBasePairAnalyzer fr3dBasePairAnalyzer;
    private final BPNetBasePairAnalyzer bpNetBasePairAnalyzer;
    private final BarnabaBasePairAnalyzer barnabaBasePairAnalyzer;

    public BasePairAnalyzer provideBasePairAnalyzer(AnalysisTool basePairAnalyzerEnum) {
        switch (basePairAnalyzerEnum) {
            case BPNET:
                return bpNetBasePairAnalyzer;
            case BARNABA:
                return barnabaBasePairAnalyzer;
            case RNAVIEW:
                return rnaViewBasePairAnalyzer;
            case FR3D_PYTHON:
                return fr3dBasePairAnalyzer;
            case MC_ANNOTATE:
                return mcAnnotateBasePairAnalyzer;
            default:
                throw new RuntimeException("unhandled enum passed to provideBasePairAnalyzer method");
        }
    }

    @Autowired
    public BasePairLoader(RnaViewBasePairAnalyzer rnaViewBasePairAnalyzer,
                          MCAnnotateBasePairAnalyzer mcAnnotateBasePairAnalyzer,
                          Fr3dBasePairAnalyzer fr3dBasePairAnalyzer,
                          BPNetBasePairAnalyzer bpNetBasePairAnalyzer,
                          BarnabaBasePairAnalyzer barnabaBasePairAnalyzer) {
        this.rnaViewBasePairAnalyzer = rnaViewBasePairAnalyzer;
        this.mcAnnotateBasePairAnalyzer = mcAnnotateBasePairAnalyzer;
        this.fr3dBasePairAnalyzer = fr3dBasePairAnalyzer;
        this.bpNetBasePairAnalyzer = bpNetBasePairAnalyzer;
        this.barnabaBasePairAnalyzer = barnabaBasePairAnalyzer;
    }
}
