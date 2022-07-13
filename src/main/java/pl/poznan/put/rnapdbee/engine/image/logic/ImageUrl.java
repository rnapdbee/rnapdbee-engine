package pl.poznan.put.rnapdbee.engine.image.logic;

import edu.put.rnapdbee.enums.DrawerEnum;
import edu.put.rnapdbee.enums.DrawingResult;
import edu.put.rnapdbee.visualization.SecondaryStructureImage;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.structure.formats.DotBracket;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Class extending SecondaryStructureImage with svgUrl and pngUrl fields, containing URLs to PNG and SVG picture
 */
// TODO analyse what is needed here
public class ImageUrl extends SecondaryStructureImage implements Serializable {
    private static final long serialVersionUID = -2570661455312387449L;

    private final String svgUrl;
    private final String pngUrl;

    public ImageUrl(
            final DotBracket dotBracket,
            final DrawerEnum successfulDrawer,
            final DrawerEnum failedDrawer,
            final DrawingResult drawingResult,
            final SVGDocument image,
            final ServletContext context)
            throws IOException {
        super(dotBracket, successfulDrawer, failedDrawer, drawingResult, image);
        final Pair<File, String> svgFileUrl = ImageUtils.generateSvgUrl(context, image);
        final Pair<File, String> pngFileUrl = ImageUtils.generatePngUrl(context, image);
        svgUrl = svgFileUrl.getRight();
        pngUrl = pngFileUrl.getRight();
    }

    public ImageUrl(final SecondaryStructureImage image, final ServletContext context)
            throws IOException {
        super(image);
        final Pair<File, String> svgFileUrl = ImageUtils.generateSvgUrl(context, image.getImage());
        final Pair<File, String> pngFileUrl = ImageUtils.generatePngUrl(context, image.getImage());
        svgUrl = svgFileUrl.getRight();
        pngUrl = pngFileUrl.getRight();
    }

    @Override
    public final String getSvgUrl() {
        return svgUrl;
    }

    @Override
    public final String getPngUrl() {
        return pngUrl;
    }
}
