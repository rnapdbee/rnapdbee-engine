package pl.poznan.put.rnapdbee.engine.shared.image.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class representing structure of information about image output
 */
public class ImageInformationOutput {

    /**
     * svg File is stored and sent to client as base64 byte array.
     */
    @JsonProperty("svgFile")
    private byte[] svgFile;
    @JsonProperty("successfulVisualizationTool")
    private VisualizationTool successfulVisualizationTool;
    @JsonProperty("failedVisualizationTool")
    private VisualizationTool failedVisualizationTool;
    @JsonProperty("drawingResult")
    private DrawingResult drawingResult;

    public static ImageInformationOutput FAILED_INSTANCE = new ImageInformationOutput()
            .withSuccessfulDrawer(VisualizationTool.NONE)
            .withFailedDrawer(VisualizationTool.NONE)
            .withDrawingResult(DrawingResult.FAILED_BY_BOTH_DRAWERS)
            .withSvgFile(null);

    public static ImageInformationOutput EMPTY_INSTANCE = new ImageInformationOutput()
            .withSuccessfulDrawer(VisualizationTool.NONE)
            .withFailedDrawer(VisualizationTool.NONE)
            .withDrawingResult(DrawingResult.NOT_DRAWN)
            .withSvgFile(null);

    public VisualizationTool getSuccessfulVisualizationTool() {
        return successfulVisualizationTool;
    }

    public VisualizationTool getFailedVisualizationTool() {
        return failedVisualizationTool;
    }

    public byte[] getSvgFile() {
        return svgFile;
    }

    public DrawingResult getDrawingResult() {
        return drawingResult;
    }

    public ImageInformationOutput withSuccessfulDrawer(VisualizationTool successfulDrawer) {
        this.successfulVisualizationTool = successfulDrawer;
        return this;
    }

    public ImageInformationOutput withFailedDrawer(VisualizationTool failedDrawer) {
        this.failedVisualizationTool = failedDrawer;
        return this;
    }

    public ImageInformationOutput withSvgFile(byte[] svgFile) {
        this.svgFile = svgFile;
        return this;
    }

    public ImageInformationOutput withDrawingResult(DrawingResult drawingResult) {
        this.drawingResult = drawingResult;
        return this;
    }
}
