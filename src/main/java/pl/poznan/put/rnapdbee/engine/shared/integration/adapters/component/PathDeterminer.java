package pl.poznan.put.rnapdbee.engine.shared.integration.adapters.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.infrastructure.configuration.RnaPDBeeAdaptersProperties;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;

@Component
public class PathDeterminer {

    private final RnaPDBeeAdaptersProperties properties;

    public String determinePath(AnalysisTool analysisTool) {
        switch (analysisTool) {
            case MC_ANNOTATE:
                return properties.getMcAnnotatePath();
            case RNAVIEW:
                return properties.getRnaViewPath();
            case BARNABA:
                return properties.getBarnabaPath();
            case BPNET:
                return properties.getBpnetPath();
            case RNAPOLIS:
                return properties.getRnapolisPath();
            case FR3D_PYTHON:
                return properties.getFr3dPath();
            default:
                throw new IllegalArgumentException("received analysisTool not supported by rnapdbee-adapters");
        }
    }

    public String determinePath(VisualizationTool visualizationTool) {
        switch (visualizationTool) {
            case R_CHIE:
                return properties.getRChiePath();
            case PSEUDO_VIEWER:
                return properties.getPseudoViewerPath();
            case RNA_PUZZLER:
                return properties.getRnaPuzzlerPath();
            default:
                throw new IllegalArgumentException("received visualizationTool not supported by rnapdbee-adapters");
        }
    }

    @Autowired
    public PathDeterminer(RnaPDBeeAdaptersProperties properties) {
        this.properties = properties;
    }
}
