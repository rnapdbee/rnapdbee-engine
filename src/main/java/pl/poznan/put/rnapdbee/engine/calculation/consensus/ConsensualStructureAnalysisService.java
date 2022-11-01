package pl.poznan.put.rnapdbee.engine.calculation.consensus;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMulti;
import pl.poznan.put.rnapdbee.engine.calculation.consensus.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.Output2D;
import pl.poznan.put.rnapdbee.engine.shared.basepair.boundary.BasePairAnalyzer;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;
import pl.poznan.put.rnapdbee.engine.shared.basepair.service.BasePairAnalyzerFactory;
import pl.poznan.put.rnapdbee.engine.shared.converter.KnotRemoval;
import pl.poznan.put.rnapdbee.engine.shared.converter.RNAStructure;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputType;
import pl.poznan.put.rnapdbee.engine.shared.domain.ModelSelection;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.shared.parser.TertiaryFileParser;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.ImmutableDefaultDotBracket;
import pl.poznan.put.structure.formats.ImmutableDefaultDotBracketFromPdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service which purpose is to handle 3D -> Multi 2D analysis.
 */
@Component
public class ConsensualStructureAnalysisService {

    private final ImageService imageService;

    private final TertiaryFileParser tertiaryFileParser;

    private final BasePairAnalyzerFactory basePairAnalyzerFactory;

    /**
     * Performs 3D -> multi 2D analysis.
     *
     * @param modelSelection      enum indicating if first, or all models from file should be analyzed
     * @param includeNonCanonical boolean flag indicating if non-canonical pairs should be kept in analysis
     * @param removeIsolated      boolean flag indicating if isolated pairs should be removed from analysis
     * @param visualizationTool   enum indicating the tool/method that should be used in visualization
     * @param filename            name of the analyzed file
     * @param content             content of the analyzed file
     * @return output of the analysis
     */
    public OutputMulti analyze(ModelSelection modelSelection,
                               boolean includeNonCanonical,
                               boolean removeIsolated,
                               VisualizationTool visualizationTool,
                               String filename,
                               String content) {
        final var analyzerPairs = prepareAnalyzerPairs();

        return findConsensus(modelSelection,
                determineInputType(filename),
                content,
                analyzerPairs,
                includeNonCanonical,
                removeIsolated,
                visualizationTool);
    }

    // TODO: refactor to streams
    private OutputMulti findConsensus(
            final ModelSelection modelSelection,
            final InputType inputType,
            final String fileContents,
            final Iterable<? extends Pair<AnalysisTool, BasePairAnalyzer>> analyzerPairs,
            final boolean includeNonCanonical,
            final boolean removeIsolated,
            VisualizationTool visualizationTool) {
        String title = null;

        final List<? extends PdbModel> models = tertiaryFileParser.parseFileContents(inputType, fileContents);

        if ((models.size() > 1) && (modelSelection == ModelSelection.FIRST)) {
            models.retainAll(Collections.singletonList(models.get(0)));
        }

        final Map<BpSeq, OutputMultiEntry> uniqueInputs = new LinkedHashMap<>();
        for (final PdbModel model : models) {
            if (!model.containsAny(MoleculeType.RNA)) {
                continue;
            }
            final PdbModel rna = model.filteredNewInstance(MoleculeType.RNA);
            title = rna.title();
            final int modelNumber = rna.modelNumber();

            for (final Pair<AnalysisTool, BasePairAnalyzer> analyzerPair : analyzerPairs) {

                final AnalysisTool analyzerEnum = analyzerPair.getLeft();
                final BasePairAnalyzer analyzer = analyzerPair.getRight();
                final BasePairAnalysis analysisResults =
                        analyzer.analyze(fileContents, includeNonCanonical, modelNumber);
                if (removeIsolated) {
                    analysisResults.removeIsolatedBasePairs(rna);
                }

                final List<AnalyzedBasePair> represented = analysisResults.getRepresented();
                final BpSeq bpSeq = BpSeq.fromBasePairs(rna.namedResidueIdentifiers(), represented);

                if (uniqueInputs.containsKey(bpSeq)) {
                    final OutputMultiEntry bpSeqInfo = uniqueInputs.get(bpSeq);
                    bpSeqInfo.getAdapterEnums().add(analyzerEnum);
                    continue;
                }

                final Ct ct = Ct.fromBpSeqAndPdbModel(bpSeq, rna);
                final DotBracket convertedDotBracket = convert(bpSeq);
                final DotBracket dotBracketFromPdb = ImmutableDefaultDotBracketFromPdb
                        .of(convertedDotBracket.sequence(), convertedDotBracket.structure(), rna);

                final ImageInformationOutput imageInformation = imageService.visualizeCanonical(visualizationTool,
                        dotBracketFromPdb);
                final List<AnalysisTool> analyzerNames = new ArrayList<>();
                analyzerNames.add(analyzerEnum);

                final Output2D output2D = new Output2D()
                        .withImageInformation(imageInformation)
                        .withBpSeqFromBpSeqObject(bpSeq)
                        .withStrandsFromDotBracket(dotBracketFromPdb)
                        .withCtFromCt(ct);

                final OutputMultiEntry outputMultiEntry = new OutputMultiEntry()
                        .withOutput2D(output2D)
                        .withAdapterEnums(analyzerNames);

                uniqueInputs.put(bpSeq, outputMultiEntry);
            }
        }

        return new OutputMulti()
                .withEntries(new ArrayList<>(uniqueInputs.values()))
                .withTitle(title);
    }

    // TODO: put in separate class with the one from tertiary
    private InputType determineInputType(String filename) {
        for (InputType inputType : InputType.values()) {
            if (filename.toLowerCase().contains(inputType.getFileExtension())) {
                return inputType;
            }
        }
        throw new IllegalArgumentException("unknown file extension provided");
    }

    // TODO: using copied DP_NEW implementation from rnapdbee-common right now, change to MILP and remove whole
    //  pl.poznan.put.rnapdbee.engine.shared.converter package!!!
    private DotBracket convert(BpSeq bpSeq) {
        RNAStructure structure = new RNAStructure(bpSeq);
        structure = KnotRemoval.dynamicProgrammingOneBest(structure);
        return ImmutableDefaultDotBracket.of(
                structure.getSequence(), structure.getDotBracketStructure());
    }

    // TODO: put in another class
    private Collection<Pair<AnalysisTool, BasePairAnalyzer>> prepareAnalyzerPairs() {
        return List.of(
                Pair.of(AnalysisTool.MC_ANNOTATE,
                        basePairAnalyzerFactory.provideBasePairAnalyzer(AnalysisTool.MC_ANNOTATE)),
                // fr3d-python is not yet mature software, disabled for now.
                // Pair.of(AnalysisTool.FR3D_PYTHON,
                //      basePairLoader.provideBasePairAnalyzer(AnalysisTool.FR3D_PYTHON)),
                Pair.of(AnalysisTool.BARNABA,
                        basePairAnalyzerFactory.provideBasePairAnalyzer(AnalysisTool.BARNABA)),
                Pair.of(AnalysisTool.BPNET,
                        basePairAnalyzerFactory.provideBasePairAnalyzer(AnalysisTool.BPNET)),
                Pair.of(AnalysisTool.RNAVIEW,
                        basePairAnalyzerFactory.provideBasePairAnalyzer(AnalysisTool.RNAVIEW))
        );
    }

    @Autowired
    public ConsensualStructureAnalysisService(ImageService imageService,
                                              TertiaryFileParser tertiaryFileParser,
                                              BasePairAnalyzerFactory basePairAnalyzerFactory) {
        this.imageService = imageService;
        this.tertiaryFileParser = tertiaryFileParser;
        this.basePairAnalyzerFactory = basePairAnalyzerFactory;
    }
}
