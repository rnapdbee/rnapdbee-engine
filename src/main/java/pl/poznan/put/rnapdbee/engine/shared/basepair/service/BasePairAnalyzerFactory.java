package pl.poznan.put.rnapdbee.engine.shared.basepair.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.BasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Component which handles loading right implementation of
 * {@link BasePairAnalyzer}
 */
@Component
public class BasePairAnalyzerFactory {
    private final List<BasePairAnalyzer> allAnalyzers;

    private Map<AnalysisTool, BasePairAnalyzer> analyzerPairs = new HashMap<>();

    /**
     * Returns the appropriate implementation of {@link BasePairAnalyzer},
     * based on given enum.
     *
     * @param basePairAnalyzerEnum enum of AnalysisTool that should be used in analysis
     * @return implementation connected with given enum
     */
    public BasePairAnalyzer provideBasePairAnalyzer(AnalysisTool basePairAnalyzerEnum) {
        if (!analyzerPairs.containsKey(basePairAnalyzerEnum)) {
            throw new IllegalArgumentException("unhandled enum passed to provideBasePairAnalyzer method");
        }

        return analyzerPairs.get(basePairAnalyzerEnum);
    }

    public Collection<Pair<AnalysisTool, BasePairAnalyzer>> prepareAnalyzerPairs() {
        return analyzerPairs
                .entrySet().stream()
                .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @PostConstruct
    private void initializeAnalyzerPairsMap() {
        analyzerPairs = allAnalyzers.stream().collect(Collectors.toMap(BasePairAnalyzer::analysisTool, Function.identity()));
    }

    @Autowired

    public BasePairAnalyzerFactory(List<BasePairAnalyzer> allAnalyzers) {
        this.allAnalyzers = allAnalyzers;
    }
}
