package pl.poznan.put.rnapdbee.engine.calculation.logic;

import edu.put.rnapdbee.visualization.DrawerPseudoViewer;
import edu.put.rnapdbee.visualization.DrawerVarnaTz;
import edu.put.rnapdbee.visualization.EmptyDrawer;
import edu.put.rnapdbee.visualization.RChieDrawer;
import edu.put.rnapdbee.visualization.SecondaryStructureDrawer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;

import java.io.File;
import java.io.IOException;

/**
 * Service class responsible for handling loading the drawers
 */
@Component
public class DrawerLoader {

    @Value("${tools.pseudoviewer}")
    private Resource pseudoviewer;

    @Value("${tools.rchie}")
    private Resource rchie;

    @Value("${system.inkscape}")
    private File inkscape;

    @Value("${system.rscript}")
    private File rscript;

    /**
     * Loads the drawer
     * @param visualizationTool enum of visualization tool
     * @return {@link SecondaryStructureDrawer} drawer
     */
    public SecondaryStructureDrawer loadDrawer(VisualizationTool visualizationTool) {
        try {
            switch (visualizationTool) {
                case PSEUDO_VIEWER:
                    return new DrawerPseudoViewer(pseudoviewer.getFile());
                case VARNA:
                    return new DrawerVarnaTz(true);
                case R_CHIE:
                    return new RChieDrawer(rscript, rchie.getFile(), inkscape);
                case NONE:
                default:
                    return new EmptyDrawer();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
