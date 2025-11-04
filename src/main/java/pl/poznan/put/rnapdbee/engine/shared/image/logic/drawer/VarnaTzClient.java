package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.rnapdbee.engine.shared.image.exception.VisualizationException;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.model.StructureData;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class VarnaTzClient {
    private static final Logger logger = LoggerFactory.getLogger(VarnaTzClient.class);
    private static final String RUN_COMMAND_PATH = "/run-command";

    private final RestTemplate restTemplate;
    private final String serviceUrl;
    private final ObjectMapper objectMapper;

    public VarnaTzClient(@Value("${varna-tz.service.url}") String serviceUrl) {
        this.restTemplate = new RestTemplate();
        this.serviceUrl = serviceUrl;
        this.objectMapper = new ObjectMapper();
        logger.info("VarnaTzClient initialized with service URL: {}", serviceUrl);
    }

    public SVGDocument draw(StructureData structureData) throws VisualizationException {
        try {
            String jsonContent = objectMapper.writeValueAsString(structureData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("arguments", "wrapper.sh");
            body.add("output_files", "clean.svg");

            ByteArrayResource fileResource =
                    new ByteArrayResource(jsonContent.getBytes(StandardCharsets.UTF_8)) {
                        @Override
                        public String getFilename() {
                            return "input.json";
                        }
                    };
            body.add("input_files", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String url = serviceUrl + RUN_COMMAND_PATH;
            logger.debug("Sending draw request to Varna-tz service at: {}", url);

            Map<String, Object> response = restTemplate.postForObject(url, requestEntity, Map.class);

            if (response != null) {
                if (response.containsKey("stdout")) {
                    logger.debug("Varna-tz stdout: {}", response.get("stdout"));
                }

                if (response.containsKey("stderr")) {
                    String stderr = (String) response.get("stderr");
                    if (stderr != null && !stderr.isEmpty()) {
                        logger.warn("Varna-tz stderr: {}", stderr);
                    }
                }

                Integer exitCode = (Integer) response.get("exit_code");
                if (exitCode != null && exitCode != 0) {
                    logger.error("Varna-tz command failed with exit code: {}", exitCode);
                    throw new VisualizationException("Varna-tz command failed with exit code: " + exitCode);
                }

                if (response.containsKey("output_files") && response.get("output_files") != null) {
                    List<Map<String, String>> outputFiles =
                            (List<Map<String, String>>) response.get("output_files");

                    for (Map<String, String> file : outputFiles) {
                        if ("clean.svg".equals(file.get("relative_path"))
                                && file.containsKey("content_base64")) {
                            byte[] decodedData = Base64.getDecoder().decode(file.get("content_base64"));
                            String svgContent = new String(decodedData, StandardCharsets.UTF_8);

                            SAXSVGDocumentFactory factory =
                                    new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
                            return factory.createSVGDocument(null, new StringReader(svgContent));
                        }
                    }
                }
                throw new VisualizationException(
                        "No clean.svg file found in the response from Varna-tz service");
            } else {
                throw new VisualizationException("Received null response from Varna-tz service");
            }
        } catch (RestClientException e) {
            throw new VisualizationException("Error communicating with Varna-tz service", e);
        } catch (Exception e) {
            throw new VisualizationException("Unexpected error during Varna-tz drawing", e);
        }
    }
}