package pl.poznan.put.rnapdbee.engine.calculation.consensus;

import org.springframework.stereotype.Component;

/**
 * Service which purpose is to handle 3D -> Multi 2D analysis.
 */
@Component
public class ConsensualStructureAnalysisService {

 /*   private final ImageService imageService;

    private final AnalysisOutputsMapper analysisOutputsMapper;

    private final BasePairAnalyzerFactory basePairAnalyzerFactory;

    // TODO: replace converter method with Mixed-Integer Linear Programming (separate Task)
    final static List<ConverterEnum> CONVERTERS = List.of(ConverterEnum.DPNEW);

    *//**
     * Performs 3D -> multi 2D analysis.
     *
     * @param modelSelection      enum indicating if first, or all models from file should be analysed
     * @param includeNonCanonical boolean flag indicating if non-canonical pairs should be kept in analysis
     * @param removeIsolated      boolean flag indicating if isolated pairs should be removed from analysis
     * @param visualizationTool   enum indicating the tool/method that should be used in visualization
     * @param filename            name of the analysed file
     * @param content             content of the analysed file
     * @return output of the analysis
     *//*
    public OutputMulti analyse(ModelSelection modelSelection,
                               boolean includeNonCanonical,
                               boolean removeIsolated,
                               VisualizationTool visualizationTool,
                               String filename,
                               String content) {
        final var analyzerPairs = prepareAnalyzerPairs();
        final var consensus = findConsensus(modelSelection,
                includeNonCanonical, removeIsolated, filename, content, analyzerPairs);

        final List<BpSeqInfo> bpSeqInfos = consensus.getLeft().getBpSeqInfos();
        final String title = bpSeqInfos.stream().findAny().orElseThrow().getTitle();

        final List<OutputMultiEntry> outputMultiEntryList = bpSeqInfos.stream()
                .map(bpSeqInfo -> mapBpSeqInfoAndConsensualImageIntoOutputMultiEntry(
                        visualizationTool,
                        bpSeqInfo))
                .collect(Collectors.toList());

        return new OutputMulti()
                .withEntries(outputMultiEntryList)
                .withTitle(title);
    }

    private Pair<ConsensusInput, ConsensusOutput> findConsensus(ModelSelection modelSelection,
                                                                boolean includeNonCanonical,
                                                                boolean removeIsolated,
                                                                String filename,
                                                                String content,
                                                                Collection<Pair<AnalysisTool, BasePairAnalyzer>>
                                                                        analyzerPairs) {
        final Pair<ConsensusInput, ConsensusOutput> consensus;
        try {
            consensus = RNApdbee.findConsensus(
                    filename,
                    determineInputType(filename),
                    content,
                    analyzerPairs,
                    CONVERTERS,
                    includeNonCanonical ? NonCanonicalHandling.TEXT_AND_VISUALIZATION : NonCanonicalHandling.IGNORE,
                    removeIsolated,
                    // TODO: restore cache (best would be using Spring mechanisms) -> do with embedding of common codebase
                    //new ParserCacheImpl(),
                    // TODO: restore cache (best would be using Spring mechanisms) -> do with embedding of common codebase
                    //new AnalyzerCacheImpl(),
                    // TODO: ugly solution, but later will be better when rnapdbee-common code is embedded into engine
                    modelSelection.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return consensus;
    }

    public static Pair<ConsensusInput, ConsensusOutput> findConsensus(
            final String structureName,
            final InputType inputType,
            final String fileContents,
            final Iterable<? extends Pair<AnalysisTool, BasePairAnalyzer>> analyzerPairs,
            final Collection<ConverterEnum> converters,
            final NonCanonicalHandling nonCanonicalHandling,
            final boolean removeIsolated,
            final ParserCache parserCache,
            final AnalyzerCache analyzerCache,
            final ModelSelection modelSelection)
            throws IOException {
        // if not cached, parse file contents
        if (!parserCache.containsKey(fileContents)) {
            final List<? extends PdbModel> models = RNApdbee.parseFileContents(inputType, fileContents);
            parserCache.put(fileContents, models);
        }
        final List<? extends PdbModel> models = parserCache.get(fileContents);

        if ((models.size() > 1) && (modelSelection == ModelSelection.FIRST)) {
            models.retainAll(Collections.singleton(models.get(0)));
        }

        final Map<BpSeq, BpSeqInfo> uniqueInputs = new LinkedHashMap<>();
        for (final PdbModel model : models) {
            if (!model.containsAny(MoleculeType.RNA)) {
                continue;
            }
            final PdbModel rna = model.filteredNewInstance(MoleculeType.RNA);
            final int modelNumber = rna.modelNumber();

            for (final Pair<BasePairAnalyzerEnum, BasePairAnalyzer> analyzerPair : analyzerPairs) {
                final BasePairAnalyzerEnum analyzerEnum = analyzerPair.getLeft();
                final String analyzerName = analyzerEnum.getDisplayName();
                final BasePairAnalyzer analyzer = analyzerPair.getRight();

                // if not cached, use analyzer on the structure
                if (!analyzerCache.containsKey(
                        fileContents, modelNumber, analyzerEnum, nonCanonicalHandling)) {
                    assert inputType.getStructureType() == StructureType.STRUCTURE_3D;
                    final AnalysisResult analysisResults =
                            analyzer.analyze(inputType, rna, fileContents, nonCanonicalHandling);
                    analyzerCache.put(
                            fileContents, modelNumber, analyzerEnum, nonCanonicalHandling, analysisResults);
                }
                final TertiaryAnalysisResult analysisResults =
                        (TertiaryAnalysisResult)
                                analyzerCache.get(fileContents, modelNumber, analyzerEnum, nonCanonicalHandling);
                if (removeIsolated) {
                    analysisResults.removeIsolatedBasePairs(rna);
                }

                final List<AnalyzedBasePair> represented = analysisResults.getRepresented();
                final List<AnalyzedBasePair> nonCanonical = analysisResults.getNonCanonical();
                final BpSeq bpSeq = BpSeq.fromBasePairs(rna.namedResidueIdentifiers(), represented);

                if (uniqueInputs.containsKey(bpSeq)) {
                    final BpSeqInfo bpSeqInfo = uniqueInputs.get(bpSeq);
                    bpSeqInfo.getBasePairAnalyzerNames().add(analyzerName);
                    continue;
                }

                final Ct ct = Ct.fromBpSeqAndPdbModel(bpSeq, rna);
                final List<DotBracketInfo> dotBracketInfos = new ArrayList<>(converters.size());

                for (final ConverterEnum converter : converters) {
                    final DotBracket dotBracket = converter.convert(bpSeq);
                    final DefaultDotBracketFromPdb dotBracketFromPdb =
                            ImmutableDefaultDotBracketFromPdb.of(
                                    dotBracket.sequence(), dotBracket.structure(), rna);
                    dotBracketInfos.add(new DotBracketInfo(converter.getName(), dotBracketFromPdb));
                }

                final List<String> analyzerNames = new ArrayList<>();
                analyzerNames.add(analyzerName);

                final BpSeqInfo bpSeqInfo =
                        new BpSeqInfo(structureName, analyzerNames, bpSeq, ct, dotBracketInfos, rna.title());

                uniqueInputs.put(bpSeq, bpSeqInfo);
            }
        }

        final ConsensusInput consensusInput = new ConsensusInput(uniqueInputs.values());
        final ConsensusFinder consensusFinder = new ArcDrawingConsensusFinder();
        return Pair.of(consensusInput, consensusFinder.findConsensus(consensusInput));
    }

    // TODO: put in separate class with the one from tertiary
    private InputType determineInputType(String filename) {
        for (InputType inputType: InputType.values()) {
            if (filename.toLowerCase().contains(inputType.getFileExtension())) {
                return inputType;
            }
        }
        throw new IllegalArgumentException("unknown file extension provided");
    }

    private OutputMultiEntry mapBpSeqInfoAndConsensualImageIntoOutputMultiEntry(VisualizationTool visualizationTool,
                                                                                BpSeqInfo bpSeqInfo) {
        // TODO: using findFirst, because in future implementation using MILP the bpSeqInfo will only contain 1 dotBracket
        //  object, refactor when rnapdbee-common code is merged with the engine's code
        final DotBracket dotBracket = bpSeqInfo.uniqueDotBrackets().keySet()
                .stream().findFirst()
                .orElseThrow(new RuntimeException());
        final ImageInformationOutput secondaryVisualization = imageService.visualizeCanonical(visualizationTool, dotBracket);

        return analysisOutputsMapper.mapBpSeqInfoAndSecondaryStructureImageIntoOutputMultiEntry(bpSeqInfo, secondaryVisualization);
    }

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
                                              AnalysisOutputsMapper analysisOutputsMapper,
                                              BasePairAnalyzerFactory basePairAnalyzerFactory) {
        this.imageService = imageService;
        this.analysisOutputsMapper = analysisOutputsMapper;
        this.basePairAnalyzerFactory = basePairAnalyzerFactory;
    }*/
}
