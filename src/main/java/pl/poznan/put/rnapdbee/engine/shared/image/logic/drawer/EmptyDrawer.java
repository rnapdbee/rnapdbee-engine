package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer;

import org.springframework.stereotype.Component;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.DotBracketFromPdb;
import pl.poznan.put.utility.svg.SVGHelper;

import java.util.List;

/**
 * Implementation of SecondaryStructureDrawer that provides empty SVG document.
 * <p>
 * The implementation has been taken from RNApdbee 2.0.
 */
@Component
public class EmptyDrawer implements SecondaryStructureDrawer {

    @Override
    public final SVGDocument drawSecondaryStructure(
            final DotBracket dotBracket, List<? extends ClassifiedBasePair> stacking) {
        return SVGHelper.emptyDocument();
    }

    @Override
    public final SVGDocument drawSecondaryStructure(
            final DotBracketFromPdb dotBracket,
            final PdbModel structureModel,
            final List<? extends ClassifiedBasePair> nonCanonicalBasePairs,
            List<? extends ClassifiedBasePair> stacking) {
        return SVGHelper.emptyDocument();
    }

    @Override
    public final VisualizationTool getEnum() {
        return VisualizationTool.NONE;
    }
}
