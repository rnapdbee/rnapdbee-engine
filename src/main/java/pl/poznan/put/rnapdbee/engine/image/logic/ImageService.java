package pl.poznan.put.rnapdbee.engine.image.logic;

import edu.put.rnapdbee.cache.ImageCache;
import edu.put.rnapdbee.cache.ImageCacheImpl;
import edu.put.rnapdbee.visualization.RNApdbeeDrawer;
import edu.put.rnapdbee.visualization.SecondaryStructureDrawer;
import edu.put.rnapdbee.visualization.SecondaryStructureImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.calculation.logic.DrawerService;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.structure.formats.DotBracket;

import javax.servlet.ServletContext;
import java.io.IOException;

/**
 * Service class responsible for visualization handling
 */
@Component
public class ImageService {

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private DrawerService drawerService;

    public SecondaryStructureImage provideVisualization(VisualizationTool visualizationTool,
                                                        DotBracket combinedStrand) {
        if (visualizationTool == VisualizationTool.NONE) {
            return SecondaryStructureImage.emptyInstance(combinedStrand);
        }

        final SecondaryStructureDrawer mainDrawer = drawerService.loadDrawer(visualizationTool);
        final SecondaryStructureDrawer backupDrawer = drawerService
                .loadDrawer(visualizationTool.getBackupVisualizationTool());

        // TODO restore the cache
        final ImageCache imageCache = new ImageCacheImpl();

        try {
            SecondaryStructureImage image = RNApdbeeDrawer.drawCanonical(imageCache, mainDrawer, backupDrawer, combinedStrand);
            return new ImageUrl(image, servletContext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
