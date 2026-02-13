package pl.poznan.put.rnapdbee.engine.shared.basepair.boundary;

import static pl.poznan.put.rnapdbee.engine.shared.basepair.domain.StackingTopology.mapToBioCommonsForm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.notation.StackingTopology;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.ImmutableDefaultPdbModel;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueCollection;
import pl.poznan.put.rna.InteractionType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.AdaptersAnalysisDTO;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairAnalysis;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePairDTO;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BasePhosphateType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.BaseRiboseType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.LeontisWesthofType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.domain.SaengerType;
import pl.poznan.put.rnapdbee.engine.shared.basepair.exception.AdaptersErrorException;
import pl.poznan.put.rnapdbee.engine.shared.domain.AnalysisTool;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RnaPDBeeAdaptersCaller;
import pl.poznan.put.rnapdbee.engine.shared.multiplet.boundary.CoplanarityClient;
import pl.poznan.put.rnapdbee.engine.shared.multiplet.BaseTriple;
import pl.poznan.put.rnapdbee.engine.shared.multiplet.MultipletSet;
import pl.poznan.put.structure.AnalyzedBasePair;
import pl.poznan.put.structure.ImmutableAnalyzedBasePair;

// TODO: WebFlux would be really efficient with the 3D->multi 2D analysis as we there perform multiple calls to the
//  adapters, it could be done in parallel and then joined up after each call is performed. We would save a tone of
//  time with this approach if the adapters were scalable horizontally in the future.
public abstract class BasePairAnalyzer {

        protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
        private final RnaPDBeeAdaptersCaller rnapdbeeAdaptersCaller;
        private final CoplanarityClient coplanarityClient;


        protected BasePairAnalyzer(RnaPDBeeAdaptersCaller rnapdbeeAdaptersCaller,
                        CoplanarityClient coplanarityClient) {
                this.rnapdbeeAdaptersCaller = rnapdbeeAdaptersCaller;
                this.coplanarityClient = coplanarityClient;
        }

        // TODO: think about using WebFlux advancements when refactoring
        public abstract BasePairAnalysis analyze(String fileContent, boolean includeNonCanonical,
                        PdbModel structureModel)
                        throws AdaptersErrorException;

        protected BasePairAnalysis performAnalysis(String fileContent, boolean includeNonCanonical,
                        PdbModel structureModel)
                        throws AdaptersErrorException {
                LOGGER.info("Base pair analysis started for model number {} with tool {}",
                                structureModel.modelNumber(), analysisTool());
                try {
                        AdaptersAnalysisDTO adaptersAnalysis = rnapdbeeAdaptersCaller.performBasePairAnalysis(
                                        fileContent, analysisTool(), structureModel.modelNumber());
                        LOGGER.debug("Received adapter response: {}", adaptersAnalysis);

                        if (adaptersAnalysis == null) {
                                throw new AdaptersErrorException("Null response from adapter");
                        }
                        if (adaptersAnalysis.getBasePairs() == null) {
                                throw new AdaptersErrorException("Null base pairs in adapter response");
                        }

                        LOGGER.info("Base pair analysis finished for model number {} with {} base pairs",
                                        structureModel.modelNumber(), adaptersAnalysis.getBasePairs().size());

                        return performPostAnalysisOnResponseFromAdapter(adaptersAnalysis, includeNonCanonical,
                                        structureModel);
                } catch (Exception e) {
                        LOGGER.error("Error during base pair analysis", e);
                        throw e;
                }
        }

        public abstract AnalysisTool analysisTool();

        /**
         * performs post analysis on response from adapter.
         * Separates response into canonical, nonCanonical, stackings, basePhosphate,
         * baseRibose, otherInteractions,
         * and interStrand lists of base pairs.
         * <p>
         * Calculates represented list of base pairs.
         * Calculates messages list.
         *
         * @param responseFromAdapter response from the adapter
         * @param includeNonCanonical flag specifying if nonCanonical pairs should be
         *                            included or not
         * @return {@link AnalyzedBasePair} BasePairAnalysis object with populated pairs
         *         lists.
         */
        protected BasePairAnalysis performPostAnalysisOnResponseFromAdapter(
                        AdaptersAnalysisDTO responseFromAdapter, boolean includeNonCanonical, PdbModel structureModel) {
                final Map<ChainNumberKey, String> pairIdentifiersWithTheirShortNames = structureModel.residues()
                                .stream()
                                .collect(Collectors.toMap(
                                                residue -> new ChainNumberKey(
                                                                residue.chainIdentifier(),
                                                                residue.residueNumber(),
                                                                residue.insertionCode().orElse(null)),
                                                residue -> String.valueOf(residue.oneLetterName())));

                List<AnalyzedBasePair> canonical = responseFromAdapter.getBasePairs().stream()
                                .filter(pair -> pair.isCanonical(structureModel))
                                .map(pair -> BasePairDTO.ofBasePairDTOWithNameFromMap(pair,
                                                pairIdentifiersWithTheirShortNames))
                                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair.toBasePair(structureModel))
                                                .withInteractionType(InteractionType.BASE_BASE)
                                                .withSaenger(basePair.getSaengerType() != null
                                                                ? SaengerType.mapToBioCommonsForm(
                                                                                basePair.getSaengerType())
                                                                : Saenger.UNKNOWN)
                                                .withLeontisWesthof(basePair.getLeontisWesthofType() != null
                                                                ? LeontisWesthofType.mapToBioCommonsForm(
                                                                                basePair.getLeontisWesthofType())
                                                                : LeontisWesthof.UNKNOWN)
                                                .withBph(BPh.UNKNOWN)
                                                .withBr(BR.UNKNOWN)
                                                .withStackingTopology(StackingTopology.UNKNOWN))
                                .collect(Collectors.toList());
                List<AnalyzedBasePair> nonCanonical = responseFromAdapter.getBasePairs().stream()
                        .filter(pair -> !pair.isCanonical(structureModel))
                                .map(pair -> BasePairDTO.ofBasePairDTOWithNameFromMap(pair,
                                                pairIdentifiersWithTheirShortNames))
                                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair.toBasePair(structureModel))
                                                .withInteractionType(InteractionType.BASE_BASE)
                                                .withSaenger(basePair.getSaengerType() != null
                                                                ? SaengerType.mapToBioCommonsForm(
                                                                                basePair.getSaengerType())
                                                                : Saenger.UNKNOWN)
                                                .withLeontisWesthof(basePair.getLeontisWesthofType() != null
                                                                ? LeontisWesthofType.mapToBioCommonsForm(
                                                                                basePair.getLeontisWesthofType())
                                                                : LeontisWesthof.UNKNOWN)
                                                .withBph(BPh.UNKNOWN)
                                                .withBr(BR.UNKNOWN)
                                                .withStackingTopology(StackingTopology.UNKNOWN))
                                .collect(Collectors.toList());
                List<AnalyzedBasePair> stackings = responseFromAdapter.getStackings().stream()
                                .map(pair -> BasePairDTO.ofBasePairDTOWithNameFromMap(pair,
                                                pairIdentifiersWithTheirShortNames))
                                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair.toBasePair(structureModel))
                                                .withInteractionType(InteractionType.STACKING)
                                                .withSaenger(Saenger.UNKNOWN)
                                                .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                                                .withBph(BPh.UNKNOWN)
                                                .withBr(BR.UNKNOWN)
                                                .withStackingTopology(mapToBioCommonsForm(basePair.getTopology())))
                                .collect(Collectors.toList());
                List<AnalyzedBasePair> basePhosphate = responseFromAdapter.getBasePhosphateInteractions().stream()
                                .map(pair -> BasePairDTO.ofBasePairDTOWithNameFromMap(pair,
                                                pairIdentifiersWithTheirShortNames))
                                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair.toBasePair(structureModel))
                                                .withInteractionType(InteractionType.BASE_PHOSPHATE)
                                                .withSaenger(Saenger.UNKNOWN)
                                                .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                                                .withBph(BasePhosphateType.mapToBioCommonsForm(basePair.getBph()))
                                                .withBr(BR.UNKNOWN)
                                                .withStackingTopology(StackingTopology.UNKNOWN))
                                .collect(Collectors.toList());
                List<AnalyzedBasePair> baseRibose = responseFromAdapter.getBaseRiboseInteractions().stream()
                                .map(pair -> BasePairDTO.ofBasePairDTOWithNameFromMap(pair,
                                                pairIdentifiersWithTheirShortNames))
                                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair.toBasePair(structureModel))
                                                .withInteractionType(InteractionType.BASE_RIBOSE)
                                                .withSaenger(Saenger.UNKNOWN)
                                                .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                                                .withBph(BPh.UNKNOWN)
                                                .withBr(BaseRiboseType.mapToBioCommonsForm(basePair.getBr()))
                                                .withStackingTopology(StackingTopology.UNKNOWN))
                                .collect(Collectors.toList());
                List<AnalyzedBasePair> otherInteractions = responseFromAdapter.getOther().stream()
                                .map(pair -> BasePairDTO.ofBasePairDTOWithNameFromMap(pair,
                                                pairIdentifiersWithTheirShortNames))
                                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair.toBasePair(structureModel))
                                                .withInteractionType(InteractionType.OTHER)
                                                .withSaenger(Saenger.UNKNOWN)
                                                .withLeontisWesthof(LeontisWesthof.UNKNOWN)
                                                .withBph(BPh.UNKNOWN)
                                                .withBr(BR.UNKNOWN)
                                                .withStackingTopology(StackingTopology.UNKNOWN))
                                .collect(Collectors.toList());
                List<AnalyzedBasePair> interStrand = responseFromAdapter.getBasePairs().stream()
                                .map(pair -> BasePairDTO.ofBasePairDTOWithNameFromMap(pair,
                                                pairIdentifiersWithTheirShortNames))
                                .map(basePair -> ImmutableAnalyzedBasePair.of(basePair.toBasePair(structureModel))
                                                .withInteractionType(InteractionType.BASE_BASE)
                                                .withSaenger(basePair.getSaengerType() != null
                                                                ? SaengerType.mapToBioCommonsForm(
                                                                                basePair.getSaengerType())
                                                                : Saenger.UNKNOWN)
                                                .withLeontisWesthof(basePair.getLeontisWesthofType() != null
                                                                ? LeontisWesthofType.mapToBioCommonsForm(
                                                                                basePair.getLeontisWesthofType())
                                                                : LeontisWesthof.UNKNOWN)
                                                .withBph(BPh.UNKNOWN)
                                                .withBr(BR.UNKNOWN)
                                                .withStackingTopology(StackingTopology.UNKNOWN))
                                .filter(basePair -> !basePair.basePair()
                                                .left()
                                                .chainIdentifier()
                                                .equals(basePair.basePair().right().chainIdentifier()))
                                .collect(Collectors.toList());
                List<AnalyzedBasePair> represented = determineRepresentedPairs(canonical, nonCanonical,
                                includeNonCanonical);

                MultipletSet multipletSet = new MultipletSet(
                                Stream.of(canonical, nonCanonical).flatMap(Collection::stream)
                                                .collect(Collectors.toList()));
                CoplanarityFilterResult coplanarityResult = filterCoplanarBaseTriples(
                                multipletSet.getBaseTriples(),
                                structureModel);
                List<BaseTriple> baseTriples = coplanarityResult.baseTriples;

                return new BasePairAnalysis.BasePairAnalysisBuilder()
                                .withRepresented(represented)
                                .withCanonical(canonical)
                                .withNonCanonical(nonCanonical)
                                .withStacking(stackings)
                                .withBasePhosphate(basePhosphate)
                                .withBaseRibose(baseRibose)
                                .withOther(otherInteractions)
                                .withInterStrand(interStrand)
                                .withBaseTriples(baseTriples)
                                .withNonCoplanarBaseTriples(coplanarityResult.filteredOut)
                                .build();
        }

	private CoplanarityFilterResult filterCoplanarBaseTriples(
			List<BaseTriple> baseTriples,
			PdbModel structureModel) {
		Map<String, String> cifContents = new LinkedHashMap<>();
		Map<String, Integer> filenameToIndex = new HashMap<>();

		for (int i = 0; i < baseTriples.size(); i++) {
			String cifContent = buildCifForBaseTriple(structureModel, baseTriples.get(i));
			if (cifContent != null) {
				String filename = "triple-" + i + ".cif";
				cifContents.put(filename, cifContent);
				filenameToIndex.put(filename, i);
			}
		}

		Map<String, Boolean> coplanarityResults = cifContents.isEmpty()
				? Map.of()
				: coplanarityClient.areBasesCoplanar(cifContents);

		List<BaseTriple> coplanar = new ArrayList<>();
		List<BaseTriple> filteredOut = new ArrayList<>();

		for (int i = 0; i < baseTriples.size(); i++) {
			String filename = "triple-" + i + ".cif";
			Boolean isCoplanar = coplanarityResults != null
					? coplanarityResults.get(filename)
					: null;
			if (Boolean.FALSE.equals(isCoplanar)) {
				filteredOut.add(baseTriples.get(i));
			} else {
				coplanar.add(baseTriples.get(i));
			}
		}

		return new CoplanarityFilterResult(coplanar, filteredOut);
	}

        private String buildCifForBaseTriple(PdbModel structureModel, BaseTriple baseTriple) {
                try {
                        PdbNamedResidueIdentifier core = baseTriple.getIdentifier();
                        PdbNamedResidueIdentifier firstPartner =
                                        baseTriple.getFirstBasePair().basePair().right();
                        PdbNamedResidueIdentifier secondPartner =
                                        baseTriple.getSecondBasePair().basePair().right();

                        PdbResidueIdentifier coreId = core.toResidueIdentifier();
                        PdbResidueIdentifier firstId = firstPartner.toResidueIdentifier();
                        PdbResidueIdentifier secondId = secondPartner.toResidueIdentifier();

                        PdbResidue coreResidue = structureModel.findResidue(coreId);
                        PdbResidue firstResidue = structureModel.findResidue(firstId);
                        PdbResidue secondResidue = structureModel.findResidue(secondId);

                        if (coreResidue == null || firstResidue == null || secondResidue == null) {
                                LOGGER.warn("Unable to locate residues for base triple {}", baseTriple);
                                return null;
                        }

                        List<PdbAtomLine> atoms = new ArrayList<>();
                        atoms.addAll(coreResidue.atoms());
                        atoms.addAll(firstResidue.atoms());
                        atoms.addAll(secondResidue.atoms());

                        PdbModel tripleModel = ImmutableDefaultPdbModel.of(atoms);
                        ResidueCollection.CifBuilder cifBuilder = new ResidueCollection.CifBuilder();
                        cifBuilder.add(tripleModel, "TRI", "base-triple");
                        return cifBuilder.build();
                } catch (Exception e) {
                        LOGGER.warn("Failed to build CIF for base triple {}", baseTriple, e);
                        return null;
                }
        }

        private static final class CoplanarityFilterResult {
                private final List<BaseTriple> baseTriples;
                private final List<BaseTriple> filteredOut;

                private CoplanarityFilterResult(List<BaseTriple> baseTriples, List<BaseTriple> filteredOut) {
                        this.baseTriples = baseTriples;
                        this.filteredOut = filteredOut;
                }
        }

	private List<AnalyzedBasePair> determineRepresentedPairs(
                        List<AnalyzedBasePair> canonicalPairs,
                        List<AnalyzedBasePair> nonCanonicalPairs,
                        boolean includeNonCanonical) {
                List<AnalyzedBasePair> pairsClassifiedAsRepresented = new ArrayList<>();
                final HashSet<PdbNamedResidueIdentifier> classifiedNucleotides = new HashSet<>();

                Consumer<AnalyzedBasePair> classifyBasePair = pair -> {
                        if (!classifiedNucleotides.contains(pair.basePair().left())
                                        && !classifiedNucleotides.contains(pair.basePair().right())) {
                                pairsClassifiedAsRepresented.add(pair);
                                /*
                                 * TODO: ask if this should stay (2.0 version has inverted pairs, however it
                                 * probably does not matter)
                                 * pairsClassifiedAsRepresented.add((AnalyzedBasePair) pair.invert());
                                 */
                                classifiedNucleotides.add(pair.basePair().left());
                                classifiedNucleotides.add(pair.basePair().right());
                        }
                };

                canonicalPairs.forEach(classifyBasePair);
                if (includeNonCanonical) {
                        nonCanonicalPairs.forEach(classifyBasePair);
                }
                return pairsClassifiedAsRepresented;
        }
}
