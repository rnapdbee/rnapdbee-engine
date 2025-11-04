package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer;

import fr.orsay.lri.varna.models.rna.ModeleBP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.notation.NucleobaseEdge;
import pl.poznan.put.notation.Stericity;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.image.exception.VisualizationException;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.model.Nucleotide;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.model.StructureData;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.DotBracketFromPdb;
import pl.poznan.put.utility.svg.SVGHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of SecondaryStructureDrawer using VARNA-based algorithm.
 * <p>
 * This implementation uses AdvancedDrawer via REST API.
 */
@Component
public class ExternalDrawerVarna implements SecondaryStructureDrawer {
    private static final Color MISSING_OUTLINE_COLOR = new Color(222, 45, 38);

    private final VarnaTzClient varnaTzClient;

    @Autowired
    public ExternalDrawerVarna(VarnaTzClient varnaTzClient) {
        this.varnaTzClient = varnaTzClient;
    }

    @Override
    public final SVGDocument drawSecondaryStructure(final DotBracket dotBracket) throws VisualizationException {
        var svgs = new ArrayList<SVGDocument>();
        for (var combinedStrand : dotBracket.combineStrands()) {
            var structureData = createStructureData(combinedStrand, Collections.emptyList());
            svgs.add(varnaTzClient.draw(structureData));
        }
        return SVGHelper.merge(svgs);
    }

    private static StructureData createStructureData(
            DotBracket combinedStrand, List<? extends ClassifiedBasePair> nonCanonicalBasePairs) {
        var nucleotides = new ArrayList<Nucleotide>();
        var symbols = combinedStrand.symbols();
        for (int i = 0; i < symbols.size(); i++) {
            var symbol = symbols.get(i);
            var nucleotide = new Nucleotide();
            nucleotide.id = symbol.index();
            if (combinedStrand instanceof DotBracketFromPdb) {
                nucleotide.number =
                        ((DotBracketFromPdb) combinedStrand).identifier(symbol).residueNumber();
            } else {
                nucleotide.number = i + 1;
            }
            nucleotide.character = String.valueOf(symbol.sequence());
            if (symbol.isMissing()) {
                nucleotide.outlineColor = String.format(
                        "%d,%d,%d",
                        MISSING_OUTLINE_COLOR.getRed(),
                        MISSING_OUTLINE_COLOR.getGreen(),
                        MISSING_OUTLINE_COLOR.getBlue());
            }
            nucleotides.add(nucleotide);
        }

        var basePairs = new ArrayList<pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.model.BasePair>();
        for (var entry : combinedStrand.pairs().entrySet()) {
            var basePair = new pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.model.BasePair();
            basePair.id1 = entry.getKey().index();
            basePair.id2 = entry.getValue().index();
            if (basePair.id1 > basePair.id2) {
                continue;
            }
            basePair.edge5 = ModeleBP.Edge.WC;
            basePair.edge3 = ModeleBP.Edge.WC;
            basePair.stericity = ModeleBP.Stericity.CIS;
            basePair.canonical = true;
            basePairs.add(basePair);
        }
        for (var classifiedBasePair : nonCanonicalBasePairs) {
            assert combinedStrand instanceof DotBracketFromPdb;
            var left = ((DotBracketFromPdb) combinedStrand)
                    .symbol(classifiedBasePair.basePair().left().toResidueIdentifier());
            var right = ((DotBracketFromPdb) combinedStrand)
                    .symbol(classifiedBasePair.basePair().right().toResidueIdentifier());
            var basePair = new pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.model.BasePair();
            basePair.id1 = left.index();
            basePair.id2 = right.index();
            basePair.edge5 = map(classifiedBasePair.leontisWesthof().edge5());
            basePair.edge3 = map(classifiedBasePair.leontisWesthof().edge3());
            basePair.stericity = map(classifiedBasePair.leontisWesthof().stericity());
            basePair.canonical = false;
            basePairs.add(basePair);
        }

        var structureData = new StructureData();
        structureData.nucleotides = nucleotides;
        structureData.basePairs = basePairs;
        structureData.stackings = Collections.emptyList();
        structureData.drawingAlgorithm = "NAVIEW";
        return structureData;
    }

    private static ModeleBP.Edge map(NucleobaseEdge edge) {
        switch (edge) {
            case WATSON_CRICK:
                return ModeleBP.Edge.WC;
            case HOOGSTEEN:
                return ModeleBP.Edge.HOOGSTEEN;
            case SUGAR:
                return ModeleBP.Edge.SUGAR;
        }
        return ModeleBP.Edge.WC; // TODO: how to handle the default case?
    }

    private static ModeleBP.Stericity map(Stericity stericity) {
        switch (stericity) {
            case CIS:
                return ModeleBP.Stericity.CIS;
            case TRANS:
                return ModeleBP.Stericity.TRANS;
        }
        return ModeleBP.Stericity.CIS; // TODO: how to handle the default case?
    }

    @Override
    public final SVGDocument drawSecondaryStructure(
            final DotBracketFromPdb dotBracket,
            final PdbModel structureModel,
            final List<? extends ClassifiedBasePair> nonCanonicalBasePairs)
            throws VisualizationException {
        final List<ClassifiedBasePair> availableNonCanonical = nonCanonicalBasePairs.stream()
                .filter(ClassifiedBasePair::isPairing)
                .collect(Collectors.toList());
        final List<DotBracketFromPdb> combinedStrands = dotBracket.combineStrands(availableNonCanonical);

        final List<SVGDocument> svgs = new ArrayList<>();
        for (final DotBracketFromPdb combinedStrand : combinedStrands) {
            var currentChains = combinedStrand.identifierSet().stream()
                    .map(PdbResidueIdentifier::chainIdentifier)
                    .collect(Collectors.toSet());
            var currentNonCanonical = nonCanonicalBasePairs.stream()
                    .filter(ClassifiedBasePair::isPairing)
                    .filter(classifiedBasePair -> currentChains.contains(
                            classifiedBasePair.basePair().left().chainIdentifier()))
                    .filter(classifiedBasePair -> currentChains.contains(
                            classifiedBasePair.basePair().right().chainIdentifier()))
                    .collect(Collectors.toList());
            var structureData = createStructureData(combinedStrand, currentNonCanonical);
            svgs.add(varnaTzClient.draw(structureData));
        }
        return SVGHelper.merge(svgs);
    }

    @Override
    public final VisualizationTool getEnum() {
        return VisualizationTool.VARNA;
    }
}
