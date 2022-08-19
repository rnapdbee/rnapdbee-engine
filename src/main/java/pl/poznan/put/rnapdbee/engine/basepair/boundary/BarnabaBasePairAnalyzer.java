package pl.poznan.put.rnapdbee.engine.basepair.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BarnabaBasePairAnalyzer extends RNApdbeeAdapterBasePairAnalyzer {

    @Autowired
    public BarnabaBasePairAnalyzer(@Value("${rnapdbee.adapters.global.barnaba.path}")
                                   String pathToMCAnnotateAdapter) {
        this.adapterURI = pathToMCAnnotateAdapter;
    }
}

