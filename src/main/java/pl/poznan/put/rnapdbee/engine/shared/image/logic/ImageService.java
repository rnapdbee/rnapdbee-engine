package pl.poznan.put.rnapdbee.engine.shared.image.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.shared.domain.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.DotBracketFromPdb;

import java.util.List;

/**
 * Service class responsible for visualization handling
 */
@Component
public class ImageService {

    private final DrawerManager drawerManager;

    public ImageInformationOutput visualizeCanonicalOrNonCanonical(
            VisualizationTool visualizationTool,
            DotBracket combinedStrand,
            DotBracketFromPdb dotBracketFromPdb,
            PdbModel structureModel,
            List<? extends ClassifiedBasePair> nonCanonicalBasePairs,
            NonCanonicalHandling nonCanonicalHandling) {
        if (visualizationTool == VisualizationTool.NONE) {
            return ImageInformationOutput.EMPTY_INSTANCE;
        }
        return nonCanonicalHandling.isVisualization()
                ? drawerManager.drawCanonicalAndNonCanonical(visualizationTool, dotBracketFromPdb, structureModel,
                nonCanonicalBasePairs)
                : drawerManager.drawCanonical(visualizationTool, combinedStrand);
    }

    public ImageInformationOutput visualizeCanonical(VisualizationTool visualizationTool,
                                                     DotBracket combinedStrand) {
        if (visualizationTool == VisualizationTool.NONE) {
            return ImageInformationOutput.EMPTY_INSTANCE;
        }
        return drawerManager.drawCanonical(visualizationTool, combinedStrand);
    }

    @Autowired
    public ImageService(DrawerManager drawerManager) {
        this.drawerManager = drawerManager;
    }
}
