package pl.poznan.put.rnapdbee.engine.calculation.tertiary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import pl.poznan.put.rnapdbee.engine.shared.basepair.exception.AdaptersErrorException;
import pl.poznan.put.rnapdbee.engine.shared.basepair.service.BasePairAnalyzerFactory;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputType;
import pl.poznan.put.rnapdbee.engine.shared.domain.InputTypeDeterminer;
import pl.poznan.put.rnapdbee.engine.shared.domain.ModelSelection;
import pl.poznan.put.rnapdbee.engine.shared.domain.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementOutput;
import pl.poznan.put.rnapdbee.engine.shared.domain.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.engine.shared.elements.StructuralElementFinder;
import pl.poznan.put.rnapdbee.engine.shared.exception.BasePairAnalysisException;
import pl.poznan.put.rnapdbee.engine.shared.exception.NoRnaModelsInFileException;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.ImageInformationOutput;
import pl.poznan.put.rnapdbee.engine.shared.image.domain.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.shared.parser.TertiaryFileParser;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DefaultDotBracketFromPdb;
import pl.poznan.put.structure.formats.DotBracket;
import pl.poznan.put.structure.formats.ImmutableDefaultDotBracketFromPdb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class TertiaryStructureAnalysisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TertiaryStructureAnalysisService.class);

    private static final String BASE_PAIR_ANALYSIS_FAILED = "Base pair analysis failed";
    private static final String BASE_PAIR_ANALYSIS_FAILED_DEBUG_FORMAT = "Base pair analysis failed for " +
            "analysisTool: %s, nonCanonicalHandling: %s, removeIsolated: %s, fileContent: %s rna: %s:";

    private final BasePairAnalyzerFactory basePairAnalyzerFactory;
    private final ImageService imageService;
    private final TertiaryFileParser tertiaryFileParser;
    private final RNAValidator rnaValidator;
    private final InputTypeDeterminer inputTypeDeterminer;
    private final Converter converter;

    public Output3D analyze(ModelSelection modelSelection,
                            AnalysisTool analysisTool,
                            NonCanonicalHandling nonCanonicalHandling,
                            boolean removeIsolated,
                            StructuralElementsHandling structuralElementsHandling,
                            VisualizationTool visualizationTool,
                            String filename,
                            String fileContent) {
        var inputType = inputTypeDeterminer.detectTertiaryInputTypeFromFileName(filename);
        return performAnalysis(modelSelection, analysisTool, nonCanonicalHandling, removeIsolated,
                structuralElementsHandling, visualizationTool, inputType, fileContent);
    }

    private Output3D performAnalysis(ModelSelection modelSelection,
                                     AnalysisTool analysisTool,
                                     NonCanonicalHandling nonCanonicalHandling,
                                     boolean removeIsolated,
                                     StructuralElementsHandling structuralElementsHandling,
                                     VisualizationTool visualizationTool,
                                     InputType inputType,
                                     String fileContent) {
        final List<? extends PdbModel> models = tertiaryFileParser.parseFileContents(inputType, fileContent);
        final List<? extends PdbModel> rnaModels = models.stream()
                .filter(pdbModel -> pdbModel.containsAny(MoleculeType.RNA)).collect(Collectors.toList());
        if (rnaModels.isEmpty()) {
            throw new NoRnaModelsInFileException();
        }

        final int modelsToBeProcessed = modelSelection == ModelSelection.FIRST
                ? 1
                : rnaModels.size();
        AtomicReference<String> title = new AtomicReference<>();
        final List<SingleTertiaryModelOutput> results = rnaModels.stream()
                .limit(modelsToBeProcessed)
                .map(pdbModel -> {
                    final PdbModel rna = pdbModel.filteredNewInstance(MoleculeType.RNA);
                    final BasePairAnalysis basePairAnalysis = handleBasePairAnalysis(analysisTool,
                            nonCanonicalHandling, removeIsolated, fileContent, rna);

                    // assuming the atoms ale always being reordered
                    final PdbModel finalModel = ChainReorderer.reorderAtoms(rna, basePairAnalysis.getRepresented());
                    final BpSeq bpSeq = BpSeq.fromBasePairs(finalModel.namedResidueIdentifiers(),
                            basePairAnalysis.getRepresented());

                    final DotBracket dotBracket = converter.convert(bpSeq);
                    final DefaultDotBracketFromPdb dotBracketFromPdb = ImmutableDefaultDotBracketFromPdb
                            .of(dotBracket.sequence(), dotBracket.structure(), finalModel);
                    title.set(finalModel.title());

                    return buildSingleModelOutputFromAnalysis(
                            analysisTool, nonCanonicalHandling, structuralElementsHandling, visualizationTool,
                            finalModel, basePairAnalysis, dotBracketFromPdb, rna);
                })
                .collect(Collectors.toList());

        return new Output3D.Output3DBuilder()
                .withModels(results)
                .withTitle(title.get())
                .build();
    }

    /**
     * Creates {@link BasePairAnalysis} using {@link BasePairAnalyzerFactory}.
     * Removes isolated pairs in regard to the removeIsolated flag.
     * Adds validation messages to the analysis.
     *
     * @param analysisTool         enum indicating which tool should be uses to perform analysis
     * @param nonCanonicalHandling enum indicating how the non-canonical pairs should be handled
     * @param removeIsolated       boolean flag indicating whether isolated pairs should be removed or not
     * @param fileContent          content of analyzed file
     * @param rna                  RNA model
     * @return complete {@link BasePairAnalysis}
     */
    private BasePairAnalysis handleBasePairAnalysis(AnalysisTool analysisTool,
                                                    NonCanonicalHandling nonCanonicalHandling,
                                                    boolean removeIsolated,
                                                    String fileContent,
                                                    PdbModel rna) {
        final BasePairAnalysis basePairAnalysis;
        try {
            basePairAnalysis = basePairAnalyzerFactory.provideBasePairAnalyzer(analysisTool)
                    .analyze(fileContent, nonCanonicalHandling.isAnalysis(), rna);
        } catch (AdaptersErrorException exception) {
            LOGGER.warn(BASE_PAIR_ANALYSIS_FAILED, exception);
            LOGGER.debug(String.format(BASE_PAIR_ANALYSIS_FAILED_DEBUG_FORMAT, analysisTool, nonCanonicalHandling,
                            removeIsolated, fileContent, rna),
                    exception);
            throw new BasePairAnalysisException();
        }

        if (removeIsolated) {
            basePairAnalysis.removeIsolatedBasePairs(rna);
        }

        return basePairAnalysis;
    }

    private SingleTertiaryModelOutput buildSingleModelOutputFromAnalysis(
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            PdbModel structureModel,
            BasePairAnalysis basePairAnalysis,
            DefaultDotBracketFromPdb dotBracket,
            PdbModel rna) {
        final BasePairAnalysis filteredResults =
                basePairAnalysis.filtered(dotBracket.identifierSet());

        ImageInformationOutput image = imageService.visualizeCanonicalOrNonCanonical(visualizationTool,
                dotBracket, structureModel, basePairAnalysis.getNonCanonical(), nonCanonicalHandling);

        final BpSeq bpseq = BpSeq.fromDotBracket(dotBracket);
        final Ct ct = Ct.fromBpSeqAndPdbModel(bpseq, structureModel);
        final List<String> messages = generateMessageLog(filteredResults, image, analysisTool, rna);

        final StructuralElementFinder structuralElementFinder =
                new StructuralElementFinder(
                        dotBracket,
                        structuralElementsHandling.canElementsEndWithPseudoknots(),
                        structuralElementsHandling.isReuseSingleStrandsFromLoopsEnabled());
        structuralElementFinder.generatePdb(structureModel);

        Output2D output2D = new Output2D.Output2DBuilder()
                .withImageInformation(image)
                .withCtFromCt(ct)
                .withBpSeqFromBpSeqObject(bpseq)
                .withStrandsFromDotBracket(dotBracket)
                .withStructuralElement(StructuralElementOutput.ofStructuralElementsFinderAndCoordinates(
                        structuralElementFinder, structuralElementFinder.getPdb()))
                .build();

        return new SingleTertiaryModelOutput.Builder()
                .withModelNumber(structureModel.modelNumber())
                .withMessages(messages)
                .withCanonicalInteractions(basePairAnalysis.getCanonical())
                .withNonCanonicalInteractions(basePairAnalysis.getNonCanonical())
                .withInterStrandInteractions(basePairAnalysis.getInterStrand())
                .withBasePhosphateInteractions(basePairAnalysis.getBasePhosphate())
                .withBaseRiboseInteractions(basePairAnalysis.getBaseRibose())
                .withStackingInteractions(basePairAnalysis.getStacking())
                .withOutput2D(output2D)
                .build();
    }

    /**
     * Generates message log out of information about output image, messages about Multiplets contained in basePairAnalysis,
     * messages generated by validation of {@link PdbModel} and information about used {@link AnalysisTool}.
     *
     * @param basePairAnalysis given basePairAnalysis, which contains influential messages
     * @param image            given image with its metadata, which are used to generate messages
     * @param analysisTool     analysis
     * @param rna              rna model which is valiated
     * @return message log generated using input parameters
     */
    private List<String> generateMessageLog(BasePairAnalysis basePairAnalysis,
                                            ImageInformationOutput image,
                                            AnalysisTool analysisTool,
                                            PdbModel rna) {
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
        messages.addAll(rnaValidator.validate(rna));
        return messages;
    }

    @Autowired
    public TertiaryStructureAnalysisService(BasePairAnalyzerFactory basePairAnalyzerFactory,
                                            ImageService imageService,
                                            TertiaryFileParser tertiaryFileParser,
                                            @Value("${templates.path}") String pathToTemplates,
                                            InputTypeDeterminer inputTypeDeterminer,
                                            Converter converter) {
        this.basePairAnalyzerFactory = basePairAnalyzerFactory;
        this.imageService = imageService;
        this.tertiaryFileParser = tertiaryFileParser;
        this.rnaValidator = new RNAValidator(loadTemplates(pathToTemplates));
        this.inputTypeDeterminer = inputTypeDeterminer;
        this.converter = converter;
    }

    private Templates loadTemplates(String pathToTemplates) {
        try (final InputStream stream = getClass().getResourceAsStream(pathToTemplates)) {
            return new Templates(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
