package pl.poznan.put.rnapdbee.engine.calculation.tertiary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rna.ChainReorderer;
import pl.poznan.put.rnapdbee.engine.calculation.secondary.domain.Output2D;
import pl.poznan.put.rnapdbee.engine.calculation.tertiary.domain.Output3D;
import pl.poznan.put.rnapdbee.engine.calculation.tertiary.domain.SingleTertiaryModelOutput;
import pl.poznan.put.rnapdbee.engine.calculation.tertiary.validator.RNAValidator;
import pl.poznan.put.rnapdbee.engine.calculation.tertiary.validator.Templates;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;
import pl.poznan.put.rnapdbee.engine.shared.basepair.service.BasePairAnalyzerFactory;
import pl.poznan.put.rnapdbee.engine.shared.converter.KnotRemoval;
import pl.poznan.put.rnapdbee.engine.shared.converter.RNAStructure;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputType;
import pl.poznan.put.rnapdbee.engine.shared.domain.ModelSelection;
import pl.poznan.put.rnapdbee.engine.shared.domain.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementOutput;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.engine.shared.elements.StructuralElementFinder;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.shared.parser.TertiaryFileParser;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DefaultDotBracketFromPdb;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.DotBracketFromPdb;
import pl.poznan.put.structure.formats.ImmutableDefaultDotBracket;
import pl.poznan.put.structure.formats.ImmutableDefaultDotBracketFromPdb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TertiaryStructureAnalysisService {

    private final BasePairAnalyzerFactory basePairAnalyzerFactory;
    private final ImageService imageService;
    private final TertiaryFileParser tertiaryFileParser;

    private final Templates templates;

    public Output3D analyze(ModelSelection modelSelection,
                            AnalysisTool analysisTool,
                            NonCanonicalHandling nonCanonicalHandling,
                            boolean removeIsolated,
                            StructuralElementsHandling structuralElementsHandling,
                            VisualizationTool visualizationTool,
                            String filename,
                            String fileContent) {
        return performAnalysis(modelSelection, analysisTool, nonCanonicalHandling, removeIsolated,
                structuralElementsHandling, visualizationTool, filename, fileContent);
    }

    private Output3D performAnalysis(ModelSelection modelSelection,
                                     AnalysisTool analysisTool,
                                     NonCanonicalHandling nonCanonicalHandling,
                                     boolean removeIsolated,
                                     StructuralElementsHandling structuralElementsHandling,
                                     VisualizationTool visualizationTool,
                                     String filename,
                                     String fileContent) {
        final List<? extends PdbModel> models;
        String title = null;
        try {
            models = tertiaryFileParser.parseFileContents(determineInputType(filename), fileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if ((models.size() > 1) && (modelSelection == ModelSelection.FIRST)) {
            models.retainAll(Collections.singleton(models.get(0)));
        }
        // TODO: is validating needed?
        final RNAValidator validator = new RNAValidator(templates);
        final List<SingleTertiaryModelOutput> results = new ArrayList<>(models.size());

        for (final PdbModel model : models) {
            if (!model.containsAny(MoleculeType.RNA)) {
                continue;
            }
            final PdbModel rna = model.filteredNewInstance(MoleculeType.RNA);
            final int modelNumber = rna.modelNumber();

            final BasePairAnalysis basePairAnalysis = basePairAnalyzerFactory.provideBasePairAnalyzer(analysisTool)
                    .analyze(fileContent, nonCanonicalHandling.isAnalysis(), modelNumber);

            // handle isolated base pairs
            if (removeIsolated) {
                basePairAnalysis.removeIsolatedBasePairs(rna);
            }

            // add validation messages
            final List<String> validationMessages = validator.validate(rna);
            basePairAnalysis.insertMessages(validationMessages);

            // assuming the atoms ale always being reordered
            final PdbModel finalModel = ChainReorderer.reorderAtoms(rna, basePairAnalysis.getRepresented());
            title = finalModel.title();
            final BpSeq bpSeq =
                    BpSeq.fromBasePairs(
                            finalModel.namedResidueIdentifiers(), basePairAnalysis.getRepresented());

            // convert to dot-bracket
            final DotBracket dotBracket = convert(bpSeq);
            final DefaultDotBracketFromPdb dotBracketFromPdb = ImmutableDefaultDotBracketFromPdb
                    .of(dotBracket.sequence(), dotBracket.structure(), finalModel);

            final List<DotBracketFromPdb> combinedStrands = determineCombinedStrands(nonCanonicalHandling,
                    basePairAnalysis.getNonCanonical(), dotBracketFromPdb);

            combinedStrands.forEach(combinedStrand -> {
                var singleResult = processSingleCombinedStrand(
                        analysisTool, nonCanonicalHandling, structuralElementsHandling, visualizationTool,
                        finalModel, basePairAnalysis, dotBracketFromPdb, combinedStrand);
                results.add(singleResult);
            });
        }

        Output3D output3D = new Output3D();
        output3D.setTitle(title);
        output3D.setModels(results);

        return output3D;
    }

    private SingleTertiaryModelOutput processSingleCombinedStrand(
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            PdbModel structureModel,
            BasePairAnalysis basePairAnalysis,
            DefaultDotBracketFromPdb dotBracket,
            DotBracketFromPdb combinedStrand) {
        final BasePairAnalysis filteredResults =
                basePairAnalysis.filtered(combinedStrand.identifierSet());

        ImageInformationOutput image = imageService.visualizeCanonicalOrNonCanonical(visualizationTool,
                combinedStrand, dotBracket, structureModel, basePairAnalysis.getNonCanonical(), nonCanonicalHandling);

        final BpSeq bpseq = BpSeq.fromDotBracket(combinedStrand);
        // todo: maybe fromBpSeq is sufficient? -> ask
        final Ct ct = Ct.fromBpSeqAndPdbModel(bpseq, structureModel);
        final List<String> messages = generateMessageLog(filteredResults, image, analysisTool);

        final StructuralElementFinder structuralElementFinder =
                new StructuralElementFinder(
                        combinedStrand,
                        structuralElementsHandling.canElementsEndWithPseudoknots(),
                        structuralElementsHandling.isReuseSingleStrandsFromLoopsEnabled());
        structuralElementFinder.generatePdb(structureModel);

        Output2D output2D = new Output2D()
                .withImageInformation(image)
                .withCtFromCt(ct)
                .withBpSeqFromBpSeqObject(bpseq)
                .withStructuralElement(StructuralElementOutput.ofStructuralElementsFinderAndCoordinates(
                        structuralElementFinder, structuralElementFinder.getPdb()));

        return new SingleTertiaryModelOutput.Builder()
                .withModelNumber(structureModel.modelNumber())
                .withMessages(messages)
                .withCanonicalInteractions(basePairAnalysis.getCanonical())
                .withNonCanonicalInteractions(basePairAnalysis.getNonCanonical())
                .withBasePhosphateInteractions(basePairAnalysis.getBasePhosphate())
                .withBaseRiboseInteractions(basePairAnalysis.getBaseRibose())
                .withStackingInteractions(basePairAnalysis.getStacking())
                .withOutput2D(output2D)
                .build();
    }

    private List<DotBracketFromPdb> determineCombinedStrands(NonCanonicalHandling nonCanonicalHandling,
                                                             List<AnalyzedBasePair> nonCanonicalBasePairs,
                                                             DefaultDotBracketFromPdb dotBracket) {
        return (nonCanonicalHandling.isAnalysis() || nonCanonicalHandling.isVisualization())
                ? dotBracket
                .combineStrands(nonCanonicalBasePairs.stream()
                        .filter(ClassifiedBasePair::isPairing)
                        .collect(Collectors.toList()))
                : dotBracket
                .combineStrands()
                .stream()
                .map(db -> (DotBracketFromPdb) db)
                .collect(Collectors.toList());
    }

    // TODO: put this and the one from multi2D to new file.
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

    private List<String> generateMessageLog(BasePairAnalysis basePairAnalysis,
                                            ImageInformationOutput image,
                                            AnalysisTool analysisTool) {
        final List<String> messages = new ArrayList<>();
        messages.add(String.format("Base-pairs identified by %s", analysisTool));

        switch (image.getDrawingResult()) {
            case DONE_BY_MAIN_DRAWER:
                messages.add(
                        String.format("Graphical image generated by %s", image.getSuccessfulVisualizationTool()));
                break;
            case DONE_BY_BACKUP_DRAWER:
                messages.add(
                        String.format(
                                "Graphical image generated by %s (failed to generate image with %s)",
                                image.getSuccessfulVisualizationTool(),
                                image.getFailedVisualizationTool()));
                break;
            case FAILED_BY_BOTH_DRAWERS:
                messages.add("Both drawers failed to generate image");
                break;
            case NOT_DRAWN:
            default:
                break;
        }

        messages.addAll(basePairAnalysis.getMessages());
        return messages;
    }

    @Autowired
    public TertiaryStructureAnalysisService(BasePairAnalyzerFactory basePairAnalyzerFactory,
                                            ImageService imageService,
                                            TertiaryFileParser tertiaryFileParser) {
        this.basePairAnalyzerFactory = basePairAnalyzerFactory;
        this.imageService = imageService;
        this.tertiaryFileParser = tertiaryFileParser;
        try (final InputStream stream =
                     TertiaryStructureAnalysisService.class.getResourceAsStream("/completeAtomNames.dict")) {
            templates = new Templates(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
