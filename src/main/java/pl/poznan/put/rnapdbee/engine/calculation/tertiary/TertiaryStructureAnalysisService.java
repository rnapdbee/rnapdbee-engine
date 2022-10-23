package pl.poznan.put.rnapdbee.engine.calculation.tertiary;

import org.springframework.stereotype.Component;

@Component
public class TertiaryStructureAnalysisService {
//
//    private final BasePairAnalyzerFactory basePairAnalyzerFactory;
//    private final ImageService imageService;
//    private final AnalysisOutputsMapper analysisOutputsMapper;
//
//    private final Templates templates;
//
//    public Output3D analyse(ModelSelection modelSelection,
//                            AnalysisTool analysisTool,
//                            NonCanonicalHandling nonCanonicalHandling,
//                            boolean removeIsolated,
//                            StructuralElementsHandling structuralElementsHandling,
//                            VisualizationTool visualizationTool,
//                            String filename,
//                            String fileContent) {
//
//        final List<PdbSecondaryStructure> secondaryStructures = performAnalysis(modelSelection, analysisTool,
//                nonCanonicalHandling, removeIsolated, filename, fileContent);
//
//        final List<SingleTertiaryModelOutput> results = secondaryStructures
//                .stream().flatMap(secondaryStructure ->
//                        processSingleSecondaryStructure(analysisTool,
//                                nonCanonicalHandling,
//                                structuralElementsHandling,
//                                visualizationTool,
//                                secondaryStructure))
//                .collect(Collectors.toList());
//        final String title = secondaryStructures.stream().findAny().orElseThrow().getModel().title();
//
//        Output3D output3D = new Output3D();
//        output3D.setModels(results);
//        output3D.setTitle(title);
//
//        return output3D;
//    }
//
//    private Stream<SingleTertiaryModelOutput> processSingleSecondaryStructure(
//            AnalysisTool analysisTool,
//            NonCanonicalHandling nonCanonicalHandling,
//            StructuralElementsHandling structuralElementsHandling,
//            VisualizationTool visualizationTool,
//            PdbSecondaryStructure secondaryStructure) {
//        final AnalysisResult basePairAnalysis = secondaryStructure.getAnalysisResult();
//        final List<AnalyzedBasePair> nonCanonicalBasePairs = basePairAnalysis.getNonCanonical();
//        final DefaultDotBracketFromPdb dotBracket = secondaryStructure.getDotBracket();
//        final List<DotBracketFromPdb> combinedStrands = determineCombinedStrands(nonCanonicalHandling,
//                nonCanonicalBasePairs, dotBracket);
//
//        return combinedStrands
//                .stream().map(combinedStrand ->
//                        processSingleCombinedStrand(analysisTool,
//                                nonCanonicalHandling,
//                                structuralElementsHandling,
//                                visualizationTool,
//                                secondaryStructure,
//                                basePairAnalysis,
//                                nonCanonicalBasePairs,
//                                dotBracket,
//                                combinedStrand));
//    }
//
//    private SingleTertiaryModelOutput processSingleCombinedStrand(
//            AnalysisTool analysisTool,
//            NonCanonicalHandling nonCanonicalHandling,
//            StructuralElementsHandling structuralElementsHandling,
//            VisualizationTool visualizationTool,
//            PdbSecondaryStructure secondaryStructure,
//            AnalysisResult basePairAnalysis,
//            List<AnalyzedBasePair> nonCanonicalBasePairs,
//            DefaultDotBracketFromPdb dotBracket,
//            DotBracketFromPdb combinedStrand) {
//        final PdbModel structureModel = secondaryStructure.getModel();
//        final AnalysisResult filteredResults =
//                basePairAnalysis.filtered(combinedStrand.identifierSet());
//
//        ImageInformationOutput image = imageService.visualizeCanonicalOrNonCanonical(visualizationTool,
//                combinedStrand, dotBracket, structureModel, nonCanonicalBasePairs, nonCanonicalHandling);
//
//        final BpSeq bpseq = BpSeq.fromDotBracket(combinedStrand);
//        final Ct ct = Ct.fromDotBracket(combinedStrand);
//        final List<String> messages = generateMessageLog(filteredResults, image, analysisTool);
//
//        final StructuralElementFinder structuralElementFinder =
//                new StructuralElementFinder(
//                        combinedStrand,
//                        structuralElementsHandling.canElementsEndWithPseudoknots(),
//                        structuralElementsHandling.isReuseSingleStrandsFromLoopsEnabled());
//        structuralElementFinder.generatePdb(structureModel);
//
//        return analysisOutputsMapper
//                .wrapSingleMulti2DAnalysisToDto(structureModel, filteredResults, image, bpseq, ct, messages,
//                        structuralElementFinder);
//    }
//
//    private List<DotBracketFromPdb> determineCombinedStrands(NonCanonicalHandling nonCanonicalHandling,
//                                                             List<AnalyzedBasePair> nonCanonicalBasePairs,
//                                                             DefaultDotBracketFromPdb dotBracket) {
//        return (nonCanonicalHandling.isAnalysis() || nonCanonicalHandling.isVisualization())
//                ? dotBracket
//                .combineStrands(nonCanonicalBasePairs.stream()
//                        .filter(ClassifiedBasePair::isPairing)
//                        .collect(Collectors.toList()))
//                : dotBracket
//                .combineStrands()
//                .stream()
//                .map(db -> (DotBracketFromPdb) db)
//                .collect(Collectors.toList());
//    }
//
//    private List<PdbSecondaryStructure> performAnalysis(ModelSelection modelSelection,
//                                                        AnalysisTool analysisTool,
//                                                        NonCanonicalHandling nonCanonicalHandling,
//                                                        boolean removeIsolated,
//                                                        String filename,
//                                                        String fileContent) {
//        final List<PdbSecondaryStructure> secondaryStructures;
//        try {
//            secondaryStructures = RNApdbee.process(
//                    determineInputType(filename),
//                    fileContent,
//                    // TODO: hardcoded for now, change when rnapdbee-common code is merged with engine's code
//                    analysisTool,
//                    basePairAnalyzerFactory.provideBasePairAnalyzer(analysisTool),
//                    // TODO: replace converter method with Mixed-Integer Linear Programming (separate Task)
//                    //ConverterEnum.DPNEW,
//                    nonCanonicalHandling,
//                    templates,
//                    removeIsolated,
//                    // TODO: restore cache
//                    //new ParserCacheImpl(),
//                    // TODO: restore cache
//                    //new AnalyzerCacheImpl(),
//                    // TODO: ugly solution, but later will be better when rnapdbee-common code is embedded into engine
//                    modelSelection);
//                    //true);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return secondaryStructures;
//    }
//
//    // TODO put this and the one from multi2D to new file.
//    private InputType determineInputType(String filename) {
//        for (InputType inputType : InputType.values()) {
//            if (filename.toLowerCase().contains(inputType.getFileExtension())) {
//                return inputType;
//            }
//        }
//        throw new IllegalArgumentException("unknown file extension provided");
//    }
//
//    private List<String> generateMessageLog(AnalysisResult basePairAnalysis,
//                                            ImageInformationOutput image,
//                                            AnalysisTool analysisTool) {
//        final List<String> messages = new ArrayList<>();
//        messages.add(String.format("Base-pairs identified by %s", analysisTool));
//
//        switch (image.getDrawingResult()) {
//            case DONE_BY_MAIN_DRAWER:
//                messages.add(
//                        String.format("Graphical image generated by %s", image.getSuccessfulVisualizationTool()));
//                break;
//            case DONE_BY_BACKUP_DRAWER:
//                messages.add(
//                        String.format(
//                                "Graphical image generated by %s (failed to generate image with %s)",
//                                image.getSuccessfulVisualizationTool(),
//                                image.getFailedVisualizationTool()));
//                break;
//            case FAILED_BY_BOTH_DRAWERS:
//                messages.add("Both drawers failed to generate image");
//                break;
//            case NOT_DRAWN:
//            default:
//                break;
//        }
//
//        messages.addAll(basePairAnalysis.getMessages());
//        return messages;
//    }
//
//    @Autowired
//    public TertiaryStructureAnalysisService(BasePairAnalyzerFactory basePairAnalyzerFactory,
//                                            ImageService imageService,
//                                            AnalysisOutputsMapper analysisOutputsMapper) {
//        this.basePairAnalyzerFactory = basePairAnalyzerFactory;
//        this.imageService = imageService;
//        this.analysisOutputsMapper = analysisOutputsMapper;
//        try (final InputStream stream =
//                     TertiaryStructureAnalysisService.class.getResourceAsStream("/completeAtomNames.dict")) {
//            templates = new Templates(stream);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
