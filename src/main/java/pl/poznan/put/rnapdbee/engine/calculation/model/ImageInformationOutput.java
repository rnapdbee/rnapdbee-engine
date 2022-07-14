package pl.poznan.put.rnapdbee.engine.calculation.model;

import edu.put.rnapdbee.enums.DrawerEnum;
import edu.put.rnapdbee.visualization.SecondaryStructureImage;

/**
 * DTO class representing structure of information about image output
 */
public class ImageInformationOutput {

    private final String pathToPNGImage;
    private final String pathToSVGImage;
    private final DrawerEnum successfulDrawer;
    private final DrawerEnum failedDrawer;

    /**
     * Maps gotten image to the output one used in REST response
     *
     * @param image {@link SecondaryStructureImage} image
     */
    public ImageInformationOutput(SecondaryStructureImage image) {
        pathToPNGImage = image.getPngUrl();
        pathToSVGImage = image.getSvgUrl();
        successfulDrawer = image.getSuccessfulDrawer();
        failedDrawer = image.getFailedDrawer();
    }

    public String getPathToPNGImage() {
        return pathToPNGImage;
    }

    public String getPathToSVGImage() {
        return pathToSVGImage;
    }

    public DrawerEnum getSuccessfulDrawer() {
        return successfulDrawer;
    }

    public DrawerEnum getFailedDrawer() {
        return failedDrawer;
    }
}
