package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer;

import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RNApdbeeAdaptersCaller;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.DotBracketFromPdb;

import java.io.IOException;
import java.util.List;

public abstract class RnaPDBeeAdaptersStructureDrawer implements SecondaryStructureDrawer {

    private final RNApdbeeAdaptersCaller rnApdbeeAdaptersCaller;

    @Override
    public SVGDocument drawSecondaryStructure(DotBracket dotBracket) throws IOException {
        return rnApdbeeAdaptersCaller.performVisualization(dotBracket, getEnum());
    }

    @Override
    public SVGDocument drawSecondaryStructure(DotBracketFromPdb dotBracket,
                                              PdbModel structureModel,
                                              List<? extends ClassifiedBasePair> nonCanonicalBasePairs)
            throws IOException {
        return rnApdbeeAdaptersCaller.performVisualization(
                dotBracket,
                structureModel,
                getEnum(),
                nonCanonicalBasePairs);
    }

    protected RnaPDBeeAdaptersStructureDrawer(RNApdbeeAdaptersCaller rnApdbeeAdaptersCaller) {
        this.rnApdbeeAdaptersCaller = rnApdbeeAdaptersCaller;
    }

}
