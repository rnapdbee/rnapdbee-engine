package pl.poznan.put.rnapdbee.engine.calculation.logic;

import edu.put.rnapdbee.analysis.AnalysisResult;
import edu.put.rnapdbee.analysis.PdbSecondaryStructure;
import edu.put.rnapdbee.analysis.RNApdbee;
import edu.put.rnapdbee.analysis.elements.StructuralElementFinder;
import edu.put.rnapdbee.cache.AnalyzerCacheImpl;
import edu.put.rnapdbee.cache.ParserCacheImpl;
import edu.put.rnapdbee.enums.BasePairAnalyzerEnum;
import edu.put.rnapdbee.enums.ConverterEnum;
import edu.put.rnapdbee.enums.InputType;
import edu.put.rnapdbee.visualization.SecondaryStructureImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rnapdbee.engine.basepair.service.BasePairLoader;
import pl.poznan.put.rnapdbee.engine.image.logic.ImageService;
import pl.poznan.put.rnapdbee.engine.image.model.VisualizationTool;
import pl.poznan.put.rnapdbee.engine.model.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.model.ModelSelection;
import pl.poznan.put.rnapdbee.engine.model.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.engine.model.Output3D;
import pl.poznan.put.rnapdbee.engine.model.StructuralElementsHandling;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Ct;
import pl.poznan.put.structure.formats.DefaultDotBracketFromPdb;
import pl.poznan.put.structure.formats.DotBracketFromPdb;
import pl.poznan.put.templates.Templates;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TertiaryStructureAnalysisService {

    private final BasePairLoader basePairLoader;
    private final ImageService imageService;

    private final Templates templates;

    public Output3D analyse(ModelSelection modelSelection,
                                  AnalysisTool analysisTool,
                                  NonCanonicalHandling nonCanonicalHandling,
                                  boolean removeIsolated,
                                  StructuralElementsHandling structuralElementsHandling,
                                  VisualizationTool visualizationTool,
                                  String filename,
                                  String fileContent) {

        final List<Output3D> results = new ArrayList<>();

        final List<PdbSecondaryStructure> secondaryStructures;
        try {
            secondaryStructures = RNApdbee.process(
                    InputType.valueOf(filename.split("\\.")[1].toUpperCase()),
                    fileContent,
                    // TODO: hardcoded for now, change when rnapdbee-common code is merged with engine's code
                    BasePairAnalyzerEnum.MCANNOTATE,
                    basePairLoader.provideBasePairAnalyzer(analysisTool),
                    // TODO: replace converter method with Mixed-Integer Linear Programming (separate Task)
                    ConverterEnum.DPNEW,
                    nonCanonicalHandling.mapTo2_0Enum(),
                    templates,
                    removeIsolated,
                    // TODO: restore cache
                    new ParserCacheImpl(),
                    // TODO: restore cache
                    new AnalyzerCacheImpl(),
                    // TODO: ugly solution, but later will be better when rnapdbee-common code is embedded into engine
                    edu.put.rnapdbee.enums.ModelSelection.valueOf(modelSelection.toString()),
                    true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (final PdbSecondaryStructure secondaryStructure : secondaryStructures) {
            final AnalysisResult basePairAnalysis = secondaryStructure.getAnalysisResult();
            final List<AnalyzedBasePair> nonCanonicalBasePairs = basePairAnalysis.getNonCanonical();
            final DefaultDotBracketFromPdb dotBracket = secondaryStructure.getDotBracket();
            final List<DotBracketFromPdb> combinedStrands =
                    (nonCanonicalHandling.mapTo2_0Enum().isAnalysis() || nonCanonicalHandling.mapTo2_0Enum().isVisualization())
                            ? dotBracket
                            .combineStrands(nonCanonicalBasePairs.stream()
                                    .filter(ClassifiedBasePair::isPairing)
                                    .collect(Collectors.toList()))
                            : dotBracket
                            .combineStrands()
                            .stream()
                            .map(db -> (DotBracketFromPdb) db)
                            .collect(Collectors.toList());

            for (final DotBracketFromPdb combinedStrand : combinedStrands) {
                final SecondaryStructureImage secondaryVisualization = imageService.provideVisualization(visualizationTool, dotBracket);

                final PdbModel structureModel = secondaryStructure.getModel();
                final AnalysisResult filteredResults =
                        basePairAnalysis.filtered(combinedStrand.identifierSet());

                SecondaryStructureImage image = imageService.provideVisualization(visualizationTool, combinedStrand,
                        dotBracket, structureModel, nonCanonicalBasePairs, nonCanonicalHandling.mapTo2_0Enum());

                final int modelNumber = structureModel.modelNumber();
                final BpSeq bpseq = BpSeq.fromDotBracket(combinedStrand);
                final Ct ct = Ct.fromDotBracket(combinedStrand);
                final List<String> messages = generateMessageLog(filteredResults, image, analysisTool);

                final StructuralElementFinder structuralElementFinder =
                        new StructuralElementFinder(
                                combinedStrand,
                                structuralElementsHandling.canElementsEndWithPseudoknots(),
                                structuralElementsHandling.isReuseSingleStrandsFromLoopsEnabled());
                structuralElementFinder.generatePdb(structureModel);

                // TODO map to engine data model.
                /*results.add(
                        ImmutableAnalysisOutput.builder()
                                .modelNumber(modelNumber)
                                .source(source)
                                .bpSeq(bpseq)
                                .ct(ct)
                                .dotBracket(combinedStrand)
                                .image(image)
                                .messages(messages)
                                .structuralElementFinder(structuralElementFinder)
                                .analysisResult(filteredResults)
                                .title(structureModel.title())
                                .build());*/
            }
        }

        return null;
    }

    private List<String> generateMessageLog(AnalysisResult basePairAnalysis, SecondaryStructureImage image, AnalysisTool analysisTool) {
        final List<String> messages = new ArrayList<>();
        messages.add(String.format("Base-pairs identified by %s", analysisTool));

        switch (image.getDrawingResult()) {
            case DONE_BY_MAIN_DRAWER:
                messages.add(
                        String.format("Graphical image generated by %s", image.getSuccessfulDrawerName()));
                break;
            case DONE_BY_BACKUP_DRAWER:
                messages.add(
                        String.format(
                                "Graphical image generated by %s (failed to generate image with %s)",
                                image.getSuccessfulDrawerName(),
                                image.getSuccessfulDrawer().backupDrawer().getDisplayName()));
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
    public TertiaryStructureAnalysisService(BasePairLoader basePairLoader, ImageService imageService) {
        this.basePairLoader = basePairLoader;
        this.imageService = imageService;
        try (final InputStream stream =
                     TertiaryStructureAnalysisService.class.getResourceAsStream("/completeAtomNames.dict")) {
            templates = new Templates(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
