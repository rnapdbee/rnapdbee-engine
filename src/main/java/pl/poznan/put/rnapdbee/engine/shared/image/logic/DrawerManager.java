package pl.poznan.put.rnapdbee.engine.shared.image.logic;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.DrawingResult;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.image.exception.VisualizationException;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.SecondaryStructureDrawer;
import pl.poznan.put.structure.AnalyzedBasePair;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(DrawerManager.class);

    private final DrawerFactory drawerFactory;

    @Autowired
    public DrawerManager(DrawerFactory drawerFactory) {
        this.drawerFactory = drawerFactory;
    }

    /**
     * Draws secondary structure with canonical pairs in SVG format.
     *
     * @param visualizationTool enum for the used visualization tool
     * @param dotBracket        dot bracket which is being visualized
     * @param stacking
     * @return populated Image Information Output instance
     */
    @Cacheable("CanonicalImage")
    // TODO: drawCanonical and drawNonCanonical differs by 2 lines and loggers,
    //  refactor to lambdas after unit tests are written.
    public ImageInformationOutput drawCanonical(
            final VisualizationTool visualizationTool, final DotBracket dotBracket, List<AnalyzedBasePair> stacking) {
        LOGGER.info(String.format(
                "Drawing canonical image started with drawer: %s, structure: %s, sequence: %s",
                visualizationTool, dotBracket.structure(), dotBracket.sequence()));
        final boolean onlyDotsMinuses = StringUtils.containsOnly(dotBracket.structure(), ".-");
        final boolean isMainToolVarna = visualizationTool == VisualizationTool.VARNA;
        final SecondaryStructureDrawer mainDrawer = drawerFactory.loadDrawer(visualizationTool);

        if (!onlyDotsMinuses || isMainToolVarna) {
            try {
                final SVGDocument svgDocument = mainDrawer.drawSecondaryStructure(dotBracket, stacking);
                // TODO: add new method for exporting SVG files, handle new type of exception there
                final byte[] svgDocumentAsByteArray = SVGHelper.export(svgDocument, Format.SVG);
                return new ImageInformationOutput()
                        .withSuccessfulDrawer(visualizationTool)
                        .withFailedDrawer(VisualizationTool.NONE)
                        .withDrawingResult(DrawingResult.DONE_BY_MAIN_DRAWER)
                        .withSvgFile(svgDocumentAsByteArray);
            } catch (final VisualizationException | IOException e) {
                LOGGER.error(
                        String.format(
                                "Failed drawing canonical image with drawer: %s, structure: %s, sequence: %s",
                                visualizationTool, dotBracket.structure(), dotBracket.sequence()),
                        e);
            }
        }

        final boolean isBackupToolVarna = visualizationTool.getBackupVisualizationTool() == VisualizationTool.VARNA;
        final VisualizationTool backupVisualizationTool = visualizationTool.getBackupVisualizationTool();
        final SecondaryStructureDrawer backupDrawer =
                drawerFactory.loadDrawer(visualizationTool.getBackupVisualizationTool());

        if (!onlyDotsMinuses || isBackupToolVarna) {
            try {
                LOGGER.info(String.format(
                        "Drawing canonical image started with backup drawer: %s, structure: %s, sequence: %s",
                        backupVisualizationTool, dotBracket.structure(), dotBracket.sequence()));
                final SVGDocument svgDocument = backupDrawer.drawSecondaryStructure(dotBracket, stacking);
                final byte[] svgDocumentAsByteArray = SVGHelper.export(svgDocument, Format.SVG);
                return new ImageInformationOutput()
                        .withSuccessfulDrawer(backupVisualizationTool)
                        .withFailedDrawer(visualizationTool)
                        .withDrawingResult(DrawingResult.DONE_BY_BACKUP_DRAWER)
                        .withSvgFile(svgDocumentAsByteArray);
            } catch (final VisualizationException | IOException e) {
                LOGGER.error(
                        String.format(
                                "Backup canonical drawing failed with drawer: %s, structure: %s, sequence: %s",
                                visualizationTool, dotBracket.structure(), dotBracket.sequence()),
                        e);
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
     * @param stacking
     * @return populated Image Information Output instance
     */
    @Cacheable("NonCanonicalImage")
    public ImageInformationOutput drawCanonicalAndNonCanonical(
            VisualizationTool visualizationTool,
            final DotBracketFromPdb dotBracket,
            final PdbModel structureModel,
            final List<? extends ClassifiedBasePair> nonCanonicalBasePairs,
            List<AnalyzedBasePair> stacking) {
        LOGGER.info(String.format(
                "Drawing non-canonical image started with drawer: %s, structure: %s, sequence: %s",
                visualizationTool, dotBracket.structure(), dotBracket.sequence()));
        final boolean onlyDotsMinuses = StringUtils.containsOnly(dotBracket.structure(), ".-");
        final boolean isMainToolVarna = visualizationTool == VisualizationTool.VARNA;
        final SecondaryStructureDrawer mainDrawer = drawerFactory.loadDrawer(visualizationTool);

        if (!onlyDotsMinuses || isMainToolVarna) {
            try {
                final SVGDocument svgDocument =
                        mainDrawer.drawSecondaryStructure(dotBracket, structureModel, nonCanonicalBasePairs, stacking);
                final byte[] svgDocumentAsByteArray = SVGHelper.export(svgDocument, Format.SVG);
                return new ImageInformationOutput()
                        .withSuccessfulDrawer(visualizationTool)
                        .withFailedDrawer(VisualizationTool.NONE)
                        .withDrawingResult(DrawingResult.DONE_BY_MAIN_DRAWER)
                        .withSvgFile(svgDocumentAsByteArray);
            } catch (final VisualizationException | IOException e) {
                LOGGER.error(
                        String.format(
                                "Failed drawing non-canonical image with drawer: %s, structure: %s, sequence: %s",
                                visualizationTool, dotBracket.structure(), dotBracket.sequence()),
                        e);
            }
        }

        final boolean isBackupToolVarna = visualizationTool.getBackupVisualizationTool() == VisualizationTool.VARNA;
        final VisualizationTool backupVisualizationTool = visualizationTool.getBackupVisualizationTool();
        final SecondaryStructureDrawer backupDrawer =
                drawerFactory.loadDrawer(visualizationTool.getBackupVisualizationTool());

        if (!onlyDotsMinuses || isBackupToolVarna) {
            try {
                LOGGER.info(String.format(
                        "Drawing non-canonical image started with backup drawer: %s, structure: %s, sequence: %s",
                        backupVisualizationTool, dotBracket.structure(), dotBracket.sequence()));
                final SVGDocument svgDocument = backupDrawer.drawSecondaryStructure(
                        dotBracket, structureModel, nonCanonicalBasePairs, stacking);
                final byte[] svgDocumentAsByteArray = SVGHelper.export(svgDocument, Format.SVG);
                return new ImageInformationOutput()
                        .withSuccessfulDrawer(backupVisualizationTool)
                        .withFailedDrawer(visualizationTool)
                        .withDrawingResult(DrawingResult.DONE_BY_BACKUP_DRAWER)
                        .withSvgFile(svgDocumentAsByteArray);
            } catch (final VisualizationException | IOException e) {
                LOGGER.error(
                        String.format(
                                "Drawing non-canonical image with drawer: %s, structure: %s, sequence: %s",
                                visualizationTool, dotBracket.structure(), dotBracket.sequence()),
                        e);
            }
        }

        return ImageInformationOutput.FAILED_INSTANCE;
    }
}
