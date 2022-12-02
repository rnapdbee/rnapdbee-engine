package pl.poznan.put.rnapdbee.engine.shared.converter.domain;

public class Node {

    private final int start;
    private final int weight;
    private int id;
    private int end;
    private int bracketing;

    public Node(int id, int start, int end, int weight) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.weight = weight;
        this.bracketing = -1;
    }

    public boolean isStartRightAfterEnd() {
        return end - start == 1;
    }

    public int getId() {
        return id;
    }

    public Node setId(int id) {
        this.id = id;
        return this;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Node setEnd(int end) {
        this.end = end;
        return this;
    }

    public int getWeight() {
        return weight;
    }

    public int getBracketing() {
        return bracketing;
    }

    public void setBracketing(int bracketing) {
        this.bracketing = bracketing;
    }
}
