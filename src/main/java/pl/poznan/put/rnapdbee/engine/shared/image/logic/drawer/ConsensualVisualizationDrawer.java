package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer;

import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.engine.shared.image.exception.VisualizationException;

import java.util.List;

/**
 * Interface which provides functionality of performing Consensual Visualization on {@link OutputMultiEntry} objects.
 */
@FunctionalInterface
public interface ConsensualVisualizationDrawer {

    byte[] performVisualization(List<OutputMultiEntry> outputMultiEntries) throws VisualizationException;

}
