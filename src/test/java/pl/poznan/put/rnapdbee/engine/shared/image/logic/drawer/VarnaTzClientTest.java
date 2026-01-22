package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.model.StructureData;

class VarnaTzClientTest {
    @Test
    void shouldParseMultipartResponse() throws Exception {
        String body = "--frame_boundary\r\n"
                + "Content-Disposition: form-data; name=\"metadata\"\r\n"
                + "Content-Type: application/json\r\n"
                + "\r\n"
                + "{\"stdout\":\"ok\",\"stderr\":\"\",\"exit_code\":0}\r\n"
                + "--frame_boundary\r\n"
                + "Content-Disposition: form-data; name=\"file\"; filename=\"clean.svg\"\r\n"
                + "Content-Type: image/svg+xml\r\n"
                + "\r\n"
                + "<svg xmlns=\"http://www.w3.org/2000/svg\"></svg>\r\n"
                + "--frame_boundary--\r\n";

        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "multipart/form-data; boundary=frame_boundary")
                    .setBody(body));
            server.start();

            VarnaTzClient client = new VarnaTzClient(server.url("/").toString());
            StructureData structureData = new StructureData();

            SVGDocument document = client.draw(structureData);

            Assertions.assertNotNull(document);
            Assertions.assertEquals("svg", document.getDocumentElement().getTagName());
        }
    }
}
