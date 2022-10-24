package pl.poznan.put.rnapdbee.engine.shared.converter;

import pl.poznan.put.structure.formats.BpSeq;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RNAStructure {
    public final List<Character> rnaSequence = new ArrayList<>();
    public final List<Integer> originalConnections = new ArrayList<>();
    public final List<Character> DotBracket = new ArrayList<>();
    public List<Pair> connections = new ArrayList<>();
    public List<Integer> correctConnections = new ArrayList<>();
    public int order = 0;
    public boolean finished = false;
    public boolean DP = false;

    // Function to create a deep copy of RNAStructure class
    public RNAStructure(final RNAStructure rhs) {
        super();
        for (int i = 0; i < rhs.connections.size(); i++) {
            connections.add(new Pair(rhs.connections.get(i)));
            rnaSequence.add(rhs.rnaSequence.get(i));
            DotBracket.add(rhs.DotBracket.get(i));
            originalConnections.add(rhs.originalConnections.get(i));
        }
        for (int i = rhs.connections.size(); i < rhs.rnaSequence.size(); i++) {
            rnaSequence.add(rhs.rnaSequence.get(i));
            DotBracket.add(rhs.DotBracket.get(i));
            originalConnections.add(rhs.originalConnections.get(i));
        }
    }

    // Initial function that analyze input file and parse lines to make connections and RNA sequence
    public RNAStructure(final String filename) {
        super();
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line);
            }
            reader.close();
        } catch (final IOException e) {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
        }
    }

    public RNAStructure(final BpSeq bpSeq) {
        super();
        for (final BpSeq.Entry e : bpSeq.entries()) {
            rnaSequence.add(e.seq());
            originalConnections.add(e.pair() - 1);
            DotBracket.add('.');
            if (e.index() < e.pair()) connections.add(new Pair(e.index(), e.pair()));
        }
    }

    public final String getSequence() {
        final StringBuilder Seq = new StringBuilder();
        for (final char c : rnaSequence) Seq.append(c);
        return Seq.toString();
    }

    public final String getStructure() {
        return originalConnections.stream()
                .mapToInt(i -> i)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    public final String getDotBracketStructure() {
        final StringBuilder Str = new StringBuilder();
        for (final char c : DotBracket) Str.append(c);
        return Str.toString();
    }

    // Save optimal structure to output file
    public final void saveFile(final String filename) {
        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            // check if Dynamic Programming algorithm was used. It returns a slightly different Protein
            // Structure
            if (DP) {
                for (int i = 0; i < rnaSequence.size(); i++) {
                    writer.write(i + 1 + " " + rnaSequence.get(i) + " " + (correctConnections.get(i) + 1));
                    writer.newLine();
                }
            } else {
                final int[] output = createOutput();

                for (int i = 0; i < rnaSequence.size(); i++) {
                    writer.write(i + 1 + " " + rnaSequence.get(i) + " " + output[i + 1]);
                    writer.newLine();
                }
            }
            writer.close();
        } catch (final IOException e) {
            System.err.format("Exception occurred trying to save '%s'.", filename);
            e.printStackTrace();
        }
    }

    // Parse line from .bpseq input to RNA sequence and connections
    private void parseLine(final String line) {
        final int index = line.indexOf(' ');
        DotBracket.add('.');
        rnaSequence.add(line.charAt(index + 1));
        final int first = Integer.parseInt(line.substring(0, index));
        final int second = Integer.parseInt(line.substring(index + 3));
        if (first < second) connections.add(new Pair(first, second));
        originalConnections.add(second - 1);
    }

    // Create temporary array from pairs for output simplicity
    private int[] createOutput() {
        final int size = rnaSequence.size();
        final int[] output = new int[size + 2];

        for (final Pair connection : connections) {
            output[connection.getFirst()] = connection.getSecond();
            output[connection.getSecond()] = connection.getFirst();
        }

        return output;
    }
}
