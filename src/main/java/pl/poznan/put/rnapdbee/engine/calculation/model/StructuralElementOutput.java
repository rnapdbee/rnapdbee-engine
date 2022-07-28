package pl.poznan.put.rnapdbee.engine.calculation.model;

import java.util.List;

/**
 * DTO class representing single Structural Element output
 */
public class StructuralElementOutput {

    private List<String> stems;
    private List<String> loops;
    private List<String> singleStrands;
    private List<String> singleStrands5p;
    private List<String> singleStrands3p;

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

    public StructuralElementOutput withStems(List<String> stems) {
        this.stems = stems;
        return this;
    }

    public StructuralElementOutput withLoops(List<String> loops) {
        this.loops = loops;
        return this;
    }

    public StructuralElementOutput withSingleStrands(List<String> singleStrands) {
        this.singleStrands = singleStrands;
        return this;
    }

    public StructuralElementOutput withSingleStrands5p(List<String> singleStrands5p) {
        this.singleStrands5p = singleStrands5p;
        return this;
    }

    public StructuralElementOutput withSingleStrands3p(List<String> singleStrands3p) {
        this.singleStrands3p = singleStrands3p;
        return this;
    }
}
