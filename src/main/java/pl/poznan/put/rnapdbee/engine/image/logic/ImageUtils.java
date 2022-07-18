package pl.poznan.put.rnapdbee.engine.image.logic;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.utility.svg.Format;
import pl.poznan.put.utility.svg.SVGHelper;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;

/**
 * ImageUtils class containing utils methods that handle generating and exporting images
 */
// TODO: analyse what is actually needed here. The class is copied from rnapdbee-web's ImageHelper class.
public final class ImageUtils {

  public static Pair<File, String> generateSvgUrl(final ServletContext context, final SVGDocument image)
          throws IOException {
    final File imageFile = ImageUtils.exportImage(context, image, Format.SVG);
    return Pair.of(
        imageFile,
        String.format("%s/resources/tmp/%s", context.getContextPath(), imageFile.getName()));
  }

  public static Pair<File, String> generatePngUrl(final ServletContext context, final SVGDocument image)
          throws IOException {
    final File imageFile = ImageUtils.exportImage(context, image, Format.PNG);
    return Pair.of(
        imageFile,
        String.format("%s/resources/tmp/%s", context.getContextPath(), imageFile.getName()));
  }

  private static File exportImage(final ServletContext context, final SVGDocument image, final Format format)
      throws IOException {
    final File directory = new File(context.getRealPath("resources/tmp"));
    FileUtils.forceMkdir(directory);
    final File imageFile = File.createTempFile("RNApdbee", '.' + format.getExtension(), directory);
    final byte[] bytes = SVGHelper.export(image, format);
    FileUtils.writeByteArrayToFile(imageFile, bytes);
    return imageFile;
  }

}
