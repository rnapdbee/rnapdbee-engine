package pl.poznan.put.rnapdbee.engine.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RnaPDBeeAdaptersProperties {

    @Value("${rnapdbee.adapters.conn-provider.max-connections}")
    private Integer maxConnections;
    @Value("${rnapdbee.adapters.conn-provider.max-idle-time-seconds}")
    private Integer maxIdleTimeSeconds;
    @Value("${rnapdbee.adapters.conn-provider.max-life-time-seconds}")
    private Integer maxLifeTimeSeconds;
    @Value("${rnapdbee.adapters.conn-provider.pending-acquire-timeout-seconds}")
    private Integer pendingAcquireTimeoutSeconds;
    @Value("${rnapdbee.adapters.conn-provider.evict-in-background-seconds}")
    private Integer evictInBackgroundSeconds;
    @Value("${rnapdbee.adapters.mono-cache-duration-in-seconds}")
    private Integer monoCacheDurationInSeconds;
    @Value("${rnapdbee.adapters.global.host}")
    private String adaptersBaseUrl;
    @Value("${rnapdbee.adapters.global.mcannotate.path}")
    private String mcAnnotatePath;
    @Value("${rnapdbee.adapters.global.bpnet.path}")
    private String bpnetPath;
    @Value("${rnapdbee.adapters.global.fr3d.path}")
    private String fr3dPath;
    @Value("${rnapdbee.adapters.global.barnaba.path}")
    private String barnabaPath;
    @Value("${rnapdbee.adapters.global.rnaview.path}")
    private String rnaViewPath;
    @Value("${rnapdbee.adapters.global.rnapolis.path}")
    private String rnapolisPath;
    @Value("${rnapdbee.adapters.global.bpseq-conversion.path}")
    private String bpseqConversionPath;
    @Value("${rnapdbee.adapters.global.weblogo.path}")
    private String weblogoPath;
    @Value("${rnapdbee.adapters.global.rchie.path}")
    private String rChiePath;
    @Value("${rnapdbee.adapters.global.pseudoviewer.path}")
    private String pseudoViewerPath;
    @Value("${rnapdbee.adapters.global.rnapuzzler.path}")
    private String rnaPuzzlerPath;

    public Integer getMaxConnections() {
        return maxConnections;
    }

    public Integer getMaxIdleTimeSeconds() {
        return maxIdleTimeSeconds;
    }

    public Integer getMaxLifeTimeSeconds() {
        return maxLifeTimeSeconds;
    }

    public Integer getPendingAcquireTimeoutSeconds() {
        return pendingAcquireTimeoutSeconds;
    }

    public Integer getEvictInBackgroundSeconds() {
        return evictInBackgroundSeconds;
    }

    public Integer getMonoCacheDurationInSeconds() {
        return monoCacheDurationInSeconds;
    }

    public String getAdaptersBaseUrl() {
        return adaptersBaseUrl;
    }

    public String getMcAnnotatePath() {
        return mcAnnotatePath;
    }

    public String getBpnetPath() {
        return bpnetPath;
    }

    public String getFr3dPath() {
        return fr3dPath;
    }

    public String getBarnabaPath() {
        return barnabaPath;
    }

    public String getRnaViewPath() {
        return rnaViewPath;
    }

    public String getRnapolisPath() {
        return rnapolisPath;
    }

    public String getBpseqConversionPath() {
        return bpseqConversionPath;
    }

    public String getWeblogoPath() {
        return weblogoPath;
    }

    public String getRChiePath() {
        return rChiePath;
    }

    public String getPseudoViewerPath() {
        return pseudoViewerPath;
    }

    public String getRnaPuzzlerPath() {
        return rnaPuzzlerPath;
    }
}
