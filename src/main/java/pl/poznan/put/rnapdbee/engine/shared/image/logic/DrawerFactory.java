package pl.poznan.put.rnapdbee.engine.shared.image.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.DrawerVarnaTz;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.EmptyDrawer;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.PseudoViewerStructureDrawer;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.RChieStructureDrawer;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.RnaPuzzlerStructureDrawer;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.SecondaryStructureDrawer;


/**
 * Service class responsible for loading correct {@link SecondaryStructureDrawer} bean.
 */
@Service
public class DrawerFactory {

    private final EmptyDrawer emptyDrawer;

    private final DrawerVarnaTz drawerVarnaTz;

    private final RChieStructureDrawer rChieStructureDrawer;

    private final PseudoViewerStructureDrawer pseudoViewerStructureDrawer;

    private final RnaPuzzlerStructureDrawer rnaPuzzlerStructureDrawer;

    /**
     * Loads the drawer
     *
     * @param visualizationTool enum of visualization tool
     * @return {@link SecondaryStructureDrawer} drawer
     */
    public SecondaryStructureDrawer loadDrawer(VisualizationTool visualizationTool) {
        switch (visualizationTool) {
            case VARNA:
                return drawerVarnaTz;
            case R_CHIE:
                return rChieStructureDrawer;
            case PSEUDO_VIEWER:
                return pseudoViewerStructureDrawer;
            case RNA_PUZZLER:
                return rnaPuzzlerStructureDrawer;
            case NONE:
            default:
                return emptyDrawer;
        }
    }

    @Autowired
    public DrawerFactory(EmptyDrawer emptyDrawer,
                         DrawerVarnaTz drawerVarnaTz,
                         RChieStructureDrawer rChieStructureDrawer,
                         PseudoViewerStructureDrawer pseudoViewerStructureDrawer,
                         RnaPuzzlerStructureDrawer rnaPuzzlerStructureDrawer) {
        this.emptyDrawer = emptyDrawer;
        this.drawerVarnaTz = drawerVarnaTz;
        this.rChieStructureDrawer = rChieStructureDrawer;
        this.pseudoViewerStructureDrawer = pseudoViewerStructureDrawer;
        this.rnaPuzzlerStructureDrawer = rnaPuzzlerStructureDrawer;
    }
}
