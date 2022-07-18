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
// TODO: remove non-needed drawers / scoop out non-drawers
@Component
public class DrawerService {

    @Value("${path.ebi.mirror}")
    private String ebiMirrorPath;

    @Value("${rcsb.ftp.hostname}")
    private String rcsbFtpHostname;

    @Value("${rcsb.ftp.path}")
    private String rcsbFtpPath;

    @Value("${tools.dssr}")
    private Resource dssr;

    @Value("${tools.fr3d}")
    private File fr3d;

    @Value("${tools.fr3d-python}")
    private File fr3dPython;

    @Value("${tools.mcannotate}")
    private Resource mcAnnotate;

    @Value("${tools.pdbx}")
    private Resource pdbx;

    @Value("${tools.pseudoviewer}")
    private Resource pseudoviewer;

    @Value("${tools.rchie}")
    private Resource rchie;

    @Value("${tools.rnaview}")
    private Resource rnaView;

    @Value("${tools.rnaview.basepars}")
    private Resource rnaViewBaseParameters;

    @Value("${system.inkscape}")
    private File inkscape;

    @Value("${system.octave}")
    private File octave;

    @Value("${system.python2}")
    private File python2;

    @Value("${system.rscript}")
    private File rscript;

    @Value("${web.examples}")
    private Resource examples;

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
