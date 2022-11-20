package pl.poznan.put.rnapdbee.engine.calculation.consensus.visualization.boundary;

import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMultiEntry;

import java.util.List;

/**
 * Interface which provides functionality of performing Consensual Visualization on {@link OutputMultiEntry} objects.
 */
@FunctionalInterface
public interface ConsensualVisualizationDrawer {

    byte[] performVisualization(List<OutputMultiEntry> outputMultiEntries);

}
