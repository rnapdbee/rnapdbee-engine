package pl.poznan.put.rnapdbee.engine.calculation.model;

import edu.put.rnapdbee.analysis.elements.StructuralElement;
import edu.put.rnapdbee.analysis.elements.StructuralElementFinder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO class representing single Structural Element output
 */
public class StructuralElementOutput {

    private final List<String> stems;
    private final List<String> loops;
    private final List<String> singleStrands;
    private final List<String> singleStrands5p;
    private final List<String> singleStrands3p;

    /**
     * Maps {@link StructuralElementFinder} into {@link StructuralElementOutput}
     *
     * @param structuralElementFinder structural element finder from analysis output
     */
    public StructuralElementOutput(StructuralElementFinder structuralElementFinder) {
        stems = structuralElementFinder.getStems().stream()
                .map(StructuralElement::toString).collect(Collectors.toList());
        loops = structuralElementFinder.getLoops().stream()
                .map(StructuralElement::toString).collect(Collectors.toList());
        singleStrands = structuralElementFinder.getSingleStrands().stream()
                .map(StructuralElement::toString).collect(Collectors.toList());
        singleStrands5p = structuralElementFinder.getSingleStrands5p().stream()
                .map(StructuralElement::toString).collect(Collectors.toList());
        singleStrands3p = structuralElementFinder.getSingleStrands3p().stream()
                .map(StructuralElement::toString).collect(Collectors.toList());
    }

    public List<String> getStems() {
        return stems;
    }

    public List<String> getLoops() {
        return loops;
    }

    public List<String> getSingleStrands() {
        return singleStrands;
    }

    public List<String> getSingleStrands5p() {
        return singleStrands5p;
    }

    public List<String> getSingleStrands3p() {
        return singleStrands3p;
    }
}
