package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer;

import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.DotBracketFromPdb;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SecondaryStructureDrawer {
    SVGDocument drawSecondaryStructure(DotBracket dotBracket)
            throws IOException;

    SVGDocument drawSecondaryStructure(
            DotBracketFromPdb dotBracket,
            PdbModel structureModel,
            List<? extends ClassifiedBasePair> nonCanonicalBasePairs)
            throws IOException;

    SVGDocument drawSecondaryStructure(
            DotBracket dotBracket, Map<DotBracketSymbol, Color> colorMap)
            throws IOException;

    VisualizationTool getEnum();
}
