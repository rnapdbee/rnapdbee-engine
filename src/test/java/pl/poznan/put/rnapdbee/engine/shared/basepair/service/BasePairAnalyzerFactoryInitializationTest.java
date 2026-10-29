package pl.poznan.put.rnapdbee.engine.shared.basepair.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.poznan.put.rnapdbee.engine.shared.converter.boundary.ExternalConverter;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Guards the {@code @PostConstruct} initialization of {@link BasePairAnalyzerFactory}.
 *
 * <p>The factory fills its lookup map in a lifecycle callback. If the annotation used by the
 * callback is not the one Spring actually processes (e.g. {@code javax} vs {@code jakarta} after a
 * framework upgrade), the bean is still created successfully and the application still starts -
 * but every request fails later with an "unhandled enum" error. The other integration tests cannot
 * catch that because they mock the factory, so this test asserts the map is really populated.</p>
 */
@SpringBootTest
class BasePairAnalyzerFactoryInitializationTest {

    @MockitoBean
    ExternalConverter externalConverter;

    @Autowired
    BasePairAnalyzerFactory basePairAnalyzerFactory;

    @Test
    void shouldPopulateAnalyzerMapOnStartup() {
        assertFalse(basePairAnalyzerFactory.prepareAnalyzerPairs().isEmpty(),
                "analyzer map is empty - the lifecycle callback did not run");
    }

    @Test
    void shouldProvideAnalyzerForEveryAnalysisTool() {
        for (AnalysisTool analysisTool : AnalysisTool.values()) {
            assertDoesNotThrow(() -> basePairAnalyzerFactory.provideBasePairAnalyzer(analysisTool),
                    "no analyzer registered for " + analysisTool);
        }
    }
}
