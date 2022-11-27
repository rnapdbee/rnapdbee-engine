package pl.poznan.put.rnapdbee.engine.infrastructure.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheConfiguration.class);

    /**
     * Clears the whole cache entries each given
     */
    @CacheEvict(value = {
            "NonCanonicalImage",
            "CanonicalImage",
            "FileContents",
            "AnalysisBarnaba",
            "AnalysisBPNet",
            "AnalysisFr3d",
            "AnalysisMCAnnotate",
            "AnalysisRNAPolis",
            "AnalysisRnaView"
    }, allEntries = true)
    @Scheduled(fixedRateString = "${scheduler.caching.remove-cache-entries.milliseconds}")
    public void emptyCache() {
        LOGGER.info("emptying application cache values");
    }
}
