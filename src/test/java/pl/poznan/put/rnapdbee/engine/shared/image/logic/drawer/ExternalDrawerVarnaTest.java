package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.junit.jupiter.api.Test;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.model.StructureData;
import pl.poznan.put.structure.formats.DefaultDotBracket;
import pl.poznan.put.structure.formats.DotBracket;

class ExternalDrawerVarnaTest {

    private static SVGDocument emptySvg() throws Exception {
        String svg = "<svg xmlns=\"http://www.w3.org/2000/svg\"></svg>";
        SAXSVGDocumentFactory factory =
                new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
        return factory.createSVGDocument(null, new StringReader(svg));
    }

    private static class FakeVarnaTzClient extends VarnaTzClient {
        private StructureData lastStructureData;

        FakeVarnaTzClient() {
            super("http://localhost");
        }

        @Override
        public SVGDocument draw(StructureData structureData) {
            this.lastStructureData = structureData;
            try {
                return emptySvg();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        StructureData getLastStructureData() {
            return lastStructureData;
        }
    }

    @Test
    void shouldRestartNumberingForEachStrand() throws Exception {
        FakeVarnaTzClient client = new FakeVarnaTzClient();
        ExternalDrawerVarna drawer = new ExternalDrawerVarna(client);
        DotBracket dotBracket = DefaultDotBracket.fromString(
                ">strand_A\nAAAGGGAAA\n...(((...\n>strand_B\nAAACCCAAA\n...)))...\n");

        drawer.drawSecondaryStructure(dotBracket, Collections.emptyList());

        StructureData structureData = client.getLastStructureData();
        List<Integer> numbers = structureData.nucleotides.stream()
                .map(nucleotide -> nucleotide.number)
                .collect(Collectors.toList());

        assertEquals(18, numbers.size());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9), numbers);
        assertEquals(List.of(8), structureData.strandBreaks);
    }

    @Test
    void shouldUseContinuousNumberingForSingleStrand() throws Exception {
        FakeVarnaTzClient client = new FakeVarnaTzClient();
        ExternalDrawerVarna drawer = new ExternalDrawerVarna(client);
        DotBracket dotBracket = DefaultDotBracket.fromString(">strand\nAAAGGGUUU\n...((()))\n");

        drawer.drawSecondaryStructure(dotBracket, Collections.emptyList());

        StructureData structureData = client.getLastStructureData();
        List<Integer> numbers = structureData.nucleotides.stream()
                .map(nucleotide -> nucleotide.number)
                .collect(Collectors.toList());

        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), numbers);
        assertEquals(Collections.emptyList(), structureData.strandBreaks);
    }
}
