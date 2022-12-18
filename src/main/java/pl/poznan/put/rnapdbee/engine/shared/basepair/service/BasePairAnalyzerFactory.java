package pl.poznan.put.rnapdbee.engine.shared.basepair.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.Fr3dBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.BPNetBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.BarnabaBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.MCAnnotateBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.BasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.RnaViewBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.RnapolisBasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;

import java.util.Collection;
import java.util.List;

/**
 * Component which handles loading right implementation of
 * {@link BasePairAnalyzer}
 */
@Component
public class BasePairAnalyzerFactory {

    private final RnaViewBasePairAnalyzer rnaViewBasePairAnalyzer;
    private final MCAnnotateBasePairAnalyzer mcAnnotateBasePairAnalyzer;
    private final Fr3dBasePairAnalyzer fr3dBasePairAnalyzer;
    private final BPNetBasePairAnalyzer bpNetBasePairAnalyzer;
    private final BarnabaBasePairAnalyzer barnabaBasePairAnalyzer;
    private final RnapolisBasePairAnalyzer rnapolisBasePairAnalyzer;

    /**
     * Returns the appropriate implementation of {@link BasePairAnalyzer},
     * based on given enum.
     *
     * @param basePairAnalyzerEnum enum of AnalysisTool that should be used in analysis
     * @return implementation connected with given enum
     */
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
            case RNAPOLIS:
                return rnapolisBasePairAnalyzer;
            default:
                throw new IllegalArgumentException("unhandled enum passed to provideBasePairAnalyzer method");
        }
    }

    public Collection<Pair<AnalysisTool, BasePairAnalyzer>> prepareAnalyzerPairs() {
        return List.of(
                Pair.of(AnalysisTool.MC_ANNOTATE, mcAnnotateBasePairAnalyzer),
                Pair.of(AnalysisTool.FR3D_PYTHON, fr3dBasePairAnalyzer),
                Pair.of(AnalysisTool.BARNABA, barnabaBasePairAnalyzer),
                Pair.of(AnalysisTool.BPNET, bpNetBasePairAnalyzer),
                Pair.of(AnalysisTool.RNAVIEW, rnaViewBasePairAnalyzer),
                Pair.of(AnalysisTool.RNAPOLIS, rnapolisBasePairAnalyzer)
        );
    }

    @Autowired
    public BasePairAnalyzerFactory(RnaViewBasePairAnalyzer rnaViewBasePairAnalyzer,
                                   MCAnnotateBasePairAnalyzer mcAnnotateBasePairAnalyzer,
                                   Fr3dBasePairAnalyzer fr3dBasePairAnalyzer,
                                   BPNetBasePairAnalyzer bpNetBasePairAnalyzer,
                                   BarnabaBasePairAnalyzer barnabaBasePairAnalyzer, RnapolisBasePairAnalyzer rnapolisBasePairAnalyzer) {
        this.rnaViewBasePairAnalyzer = rnaViewBasePairAnalyzer;
        this.mcAnnotateBasePairAnalyzer = mcAnnotateBasePairAnalyzer;
        this.fr3dBasePairAnalyzer = fr3dBasePairAnalyzer;
        this.bpNetBasePairAnalyzer = bpNetBasePairAnalyzer;
        this.barnabaBasePairAnalyzer = barnabaBasePairAnalyzer;
        this.rnapolisBasePairAnalyzer = rnapolisBasePairAnalyzer;
    }
}
