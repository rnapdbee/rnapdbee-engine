package pl.poznan.put.rnapdbee.engine.calculation.model;

import edu.put.rnapdbee.enums.DrawerEnum;

/**
 * DTO class representing structure of information about image output
 */
public class ImageInformationOutput {

    private String pathToPNGImage;
    private String pathToSVGImage;
    private DrawerEnum successfulDrawer;
    private DrawerEnum failedDrawer;

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

    public ImageInformationOutput withPathToPNGImage(String pathToPNGImage) {
        this.pathToPNGImage = pathToPNGImage;
        return this;
    }

    public ImageInformationOutput withPathToSVGImage(String pathToSVGImage) {
        this.pathToSVGImage = pathToSVGImage;
        return this;
    }

    public ImageInformationOutput withSuccessfulDrawer(DrawerEnum successfulDrawer) {
        this.successfulDrawer = successfulDrawer;
        return this;
    }

    public ImageInformationOutput withFailedDrawer(DrawerEnum failedDrawer) {
        this.failedDrawer = failedDrawer;
        return this;
    }
}
