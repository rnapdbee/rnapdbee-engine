package pl.poznan.put.rnapdbee.engine.image.logic;

import edu.put.rnapdbee.cache.ImageCache;
import edu.put.rnapdbee.cache.ImageCacheImpl;
import edu.put.rnapdbee.enums.NonCanonicalHandling;
import edu.put.rnapdbee.visualization.RNApdbeeDrawer;
import edu.put.rnapdbee.visualization.SecondaryStructureDrawer;
import edu.put.rnapdbee.visualization.SecondaryStructureImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.calculation.logic.DrawerLoader;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.DotBracketFromPdb;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.List;

/**
 * Service class responsible for visualization handling
 */
@Component
public class ImageService {

    private final ServletContext servletContext;

    private final DrawerLoader drawerLoader;

    public SecondaryStructureImage visualizeCanonicalOrNonCanonical(
            VisualizationTool visualizationTool,
            DotBracket combinedStrand,
            DotBracketFromPdb dotBracketFromPdb,
            PdbModel structureModel,
            List<? extends ClassifiedBasePair> nonCanonicalBasePairs,
            NonCanonicalHandling nonCanonicalHandling) {
        if (visualizationTool == VisualizationTool.NONE) {
            return SecondaryStructureImage.emptyInstance(combinedStrand);
        }
        return nonCanonicalHandling.isVisualization()
                ? this.visualizeNonCanonical(visualizationTool, dotBracketFromPdb, structureModel,
                nonCanonicalBasePairs)
                : this.visualizeCanonical(visualizationTool, combinedStrand);
    }

    public SecondaryStructureImage visualizeCanonical(VisualizationTool visualizationTool,
                                                      DotBracket combinedStrand) {
        if (visualizationTool == VisualizationTool.NONE) {
            return SecondaryStructureImage.emptyInstance(combinedStrand);
        }

        final SecondaryStructureDrawer mainDrawer = drawerLoader.loadDrawer(visualizationTool);
        final SecondaryStructureDrawer backupDrawer = drawerLoader
                .loadDrawer(visualizationTool.getBackupVisualizationTool());

        // TODO: restore the cache
        //  probably it would be best to use Spring cache mechanism since we're embedding rnapdbee-common into engine
        final ImageCache imageCache = new ImageCacheImpl();

        try {
            SecondaryStructureImage image = RNApdbeeDrawer.drawCanonical(imageCache, mainDrawer, backupDrawer, combinedStrand);
            return new ImageUrl(image, servletContext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SecondaryStructureImage visualizeNonCanonical(VisualizationTool visualizationTool,
                                                          DotBracketFromPdb dotBracketFromPdb,
                                                          PdbModel structureModel,
                                                          List<? extends ClassifiedBasePair> nonCanonicalBasePairs) {
        final SecondaryStructureDrawer mainDrawer = drawerLoader.loadDrawer(visualizationTool);
        final SecondaryStructureDrawer backupDrawer = drawerLoader
                .loadDrawer(visualizationTool.getBackupVisualizationTool());

        // TODO: restore the cache
        //  probably it would be best to use Spring cache mechanism since we're embedding rnapdbee-common into engine
        final ImageCache imageCache = new ImageCacheImpl();

        try {
            SecondaryStructureImage image = RNApdbeeDrawer.drawCanonicalAndNonCanonical(imageCache,
                    mainDrawer, backupDrawer, dotBracketFromPdb, structureModel, nonCanonicalBasePairs);
            return new ImageUrl(image, servletContext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public ImageService(ServletContext servletContext, DrawerLoader drawerLoader) {
        this.servletContext = servletContext;
        this.drawerLoader = drawerLoader;
    }
}
