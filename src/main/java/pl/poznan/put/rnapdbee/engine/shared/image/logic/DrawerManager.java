package pl.poznan.put.rnapdbee.engine.shared.image.logic;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.DrawingResult;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.DrawerVarnaTz;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.EmptyDrawer;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.PseudoViewerStructureDrawer;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.RChieStructureDrawer;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.RnaPuzzlerStructureDrawer;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.SecondaryStructureDrawer;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.DotBracketFromPdb;
import pl.poznan.put.utility.svg.Format;
import pl.poznan.put.utility.svg.SVGHelper;

import java.io.IOException;
import java.util.List;


/**
 * Service class responsible for managing drawers
 */
@Component
public class DrawerManager {

    private final Logger logger;

    private final EmptyDrawer emptyDrawer;

    private final DrawerVarnaTz drawerVarnaTz;

    private final RChieStructureDrawer rChieStructureDrawer;

    private final PseudoViewerStructureDrawer pseudoViewerStructureDrawer;

    private final RnaPuzzlerStructureDrawer rnaPuzzlerStructureDrawer;

    /**
     * Draws secondary structure with canonical pairs in SVG format.
     *
     * @param visualizationTool enum for the used visualization tool
     * @param dotBracket        dot bracket which is being visualized
     * @return populated Image Information Output instance
     */
    @Cacheable("CanonicalImage")
    // TODO: drawCanonical and drawNonCanonical differs by 2 lines and loggers,
    //  refactor to lambdas after unit tests are written.
    public ImageInformationOutput drawCanonical(
            final VisualizationTool visualizationTool,
            final DotBracket dotBracket) {
        logger.info(String.format("Drawing canonical image started with drawer: %s, structure: %s, sequence: %s",
                visualizationTool, dotBracket.structure(), dotBracket.sequence()));
        final boolean onlyDotsMinuses = StringUtils.containsOnly(dotBracket.structure(), ".-");
        final boolean isMainToolVarna = visualizationTool == VisualizationTool.VARNA;
        final SecondaryStructureDrawer mainDrawer = this.loadDrawer(visualizationTool);

        if (!onlyDotsMinuses || isMainToolVarna) {
            try {
                final SVGDocument svgDocument = mainDrawer.drawSecondaryStructure(dotBracket);
                final byte[] svgDocumentAsByteArray = SVGHelper.export(svgDocument, Format.SVG);
                return new ImageInformationOutput()
                        .withSuccessfulDrawer(visualizationTool)
                        .withFailedDrawer(VisualizationTool.NONE)
                        .withDrawingResult(DrawingResult.DONE_BY_MAIN_DRAWER)
                        .withSvgFile(svgDocumentAsByteArray);
            } catch (final IOException e) {
                logger.warn(String.format("Failed drawing canonical image with drawer: %s, structure: %s, sequence: %s",
                        visualizationTool, dotBracket.structure(), dotBracket.sequence()));
            }
        }

        final boolean isBackupToolVarna = visualizationTool.getBackupVisualizationTool() == VisualizationTool.VARNA;
        final VisualizationTool backupVisualizationTool = visualizationTool.getBackupVisualizationTool();
        final SecondaryStructureDrawer backupDrawer = this.loadDrawer(visualizationTool.getBackupVisualizationTool());

        if (!onlyDotsMinuses || isBackupToolVarna) {
            try {
                logger.info(String.format(
                        "Drawing canonical image started with backup drawer: %s, structure: %s, sequence: %s",
                        visualizationTool, dotBracket.structure(), dotBracket.sequence()));
                final SVGDocument svgDocument = backupDrawer.drawSecondaryStructure(dotBracket);
                final byte[] svgDocumentAsByteArray = SVGHelper.export(svgDocument, Format.SVG);
                return new ImageInformationOutput()
                        .withSuccessfulDrawer(backupVisualizationTool)
                        .withFailedDrawer(visualizationTool)
                        .withDrawingResult(DrawingResult.DONE_BY_BACKUP_DRAWER)
                        .withSvgFile(svgDocumentAsByteArray);
            } catch (final IOException e) {
                logger.error(String.format(
                        "Backup canonical drawing failed with drawer: %s, structure: %s, sequence: %s",
                        visualizationTool, dotBracket.structure(), dotBracket.sequence()));
            }
        }

        return ImageInformationOutput.FAILED_INSTANCE;
    }

    /**
     * Draws secondary structure with canonical and non-canonical pairs in SVG format.
     *
     * @param visualizationTool     enum for the desired tool
     * @param dotBracket            dot bracket which is being visualized
     * @param structureModel        pdb model which is being visualized
     * @param nonCanonicalBasePairs non-canonical pairs which are being visualized
     * @return populated Image Information Output instance
     */
    @Cacheable("NonCanonicalImage")
    public ImageInformationOutput drawCanonicalAndNonCanonical(
            VisualizationTool visualizationTool,
            final DotBracketFromPdb dotBracket,
            final PdbModel structureModel,
            final List<? extends ClassifiedBasePair> nonCanonicalBasePairs) {
        logger.info(String.format("Drawing non-canonical image started with drawer: %s, structure: %s, sequence: %s",
                visualizationTool, dotBracket.structure(), dotBracket.sequence()));
        final boolean onlyDotsMinuses = StringUtils.containsOnly(dotBracket.structure(), ".-");
        final boolean isMainToolVarna = visualizationTool == VisualizationTool.VARNA;
        final SecondaryStructureDrawer mainDrawer = this.loadDrawer(visualizationTool);

        if (!onlyDotsMinuses || isMainToolVarna) {
            try {
                final SVGDocument svgDocument = mainDrawer
                        .drawSecondaryStructure(dotBracket, structureModel, nonCanonicalBasePairs);
                final byte[] svgDocumentAsByteArray = SVGHelper.export(svgDocument, Format.SVG);
                return new ImageInformationOutput()
                        .withSuccessfulDrawer(visualizationTool)
                        .withFailedDrawer(VisualizationTool.NONE)
                        .withDrawingResult(DrawingResult.DONE_BY_MAIN_DRAWER)
                        .withSvgFile(svgDocumentAsByteArray);
            } catch (final IOException e) {
                logger.error(String.format(
                        "Failed drawing non-canonical image with drawer: %s, structure: %s, sequence: %s",
                        visualizationTool, dotBracket.structure(), dotBracket.sequence()));
            }
        }

        final boolean isBackupToolVarna = visualizationTool.getBackupVisualizationTool() == VisualizationTool.VARNA;
        final VisualizationTool backupVisualizationTool = visualizationTool.getBackupVisualizationTool();
        final SecondaryStructureDrawer backupDrawer = this.loadDrawer(visualizationTool.getBackupVisualizationTool());

        if (!onlyDotsMinuses || isBackupToolVarna) {
            try {
                logger.info(String.format(
                        "Drawing non-canonical image started with backup drawer: %s, structure: %s, sequence: %s",
                        visualizationTool, dotBracket.structure(), dotBracket.sequence()));
                final SVGDocument svgDocument = backupDrawer
                        .drawSecondaryStructure(dotBracket, structureModel, nonCanonicalBasePairs);
                final byte[] svgDocumentAsByteArray = SVGHelper.export(svgDocument, Format.SVG);
                return new ImageInformationOutput()
                        .withSuccessfulDrawer(backupVisualizationTool)
                        .withFailedDrawer(visualizationTool)
                        .withDrawingResult(DrawingResult.DONE_BY_BACKUP_DRAWER)
                        .withSvgFile(svgDocumentAsByteArray);
            } catch (final IOException e) {
                logger.error(String.format("Drawing non-canonical image with drawer: %s, structure: %s, sequence: %s",
                        visualizationTool, dotBracket.structure(), dotBracket.sequence()));
            }
        }

        return ImageInformationOutput.FAILED_INSTANCE;
    }

    /**
     * Loads the drawer
     *
     * @param visualizationTool enum of visualization tool
     * @return {@link SecondaryStructureDrawer} drawer
     */
    // TODO: move to another Factory class.
    private SecondaryStructureDrawer loadDrawer(VisualizationTool visualizationTool) {
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
    public DrawerManager(Logger logger, EmptyDrawer emptyDrawer, DrawerVarnaTz drawerVarnaTz, RChieStructureDrawer rChieStructureDrawer, PseudoViewerStructureDrawer pseudoViewerStructureDrawer, RnaPuzzlerStructureDrawer rnaPuzzlerStructureDrawer) {
        this.logger = logger;
        this.emptyDrawer = emptyDrawer;
        this.drawerVarnaTz = drawerVarnaTz;
        this.rChieStructureDrawer = rChieStructureDrawer;
        this.pseudoViewerStructureDrawer = pseudoViewerStructureDrawer;
        this.rnaPuzzlerStructureDrawer = rnaPuzzlerStructureDrawer;
    }
}
