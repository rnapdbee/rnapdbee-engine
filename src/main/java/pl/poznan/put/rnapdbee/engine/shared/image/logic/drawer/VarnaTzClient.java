package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.svg.SVGDocument;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.poznan.put.rnapdbee.engine.shared.image.exception.VisualizationException;
import pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer.model.StructureData;

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

            String url = UriComponentsBuilder.fromHttpUrl(serviceUrl)
                    .path(RUN_COMMAND_PATH)
                    .toUriString();
            logger.debug("Sending draw request to Varna-tz service at: {}", url);

            try {
                ResponseEntity<MultiValueMap<String, Object>> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        requestEntity,
                        new org.springframework.core.ParameterizedTypeReference<MultiValueMap<String, Object>>() {
                        });

                MediaType contentType = responseEntity.getHeaders().getContentType();
                if (contentType != null && MediaType.MULTIPART_FORM_DATA.includes(contentType)) {
                    return handleMultipartResponse(responseEntity.getBody());
                }
            } catch (RestClientException e) {
                logger.debug("Multipart parsing failed, falling back to JSON response parsing.", e);
            }

            Map<String, Object> response = restTemplate.postForObject(url, requestEntity, Map.class);
            return handleLegacyResponse(response);
        } catch (RestClientException e) {
            throw new VisualizationException("Error communicating with Varna-tz service", e);
        } catch (Exception e) {
            throw new VisualizationException("Unexpected error during Varna-tz drawing", e);
        }
    }

    private SVGDocument handleMultipartResponse(MultiValueMap<String, Object> parts)
            throws IOException, VisualizationException {
        if (CollectionUtils.isEmpty(parts)) {
            throw new VisualizationException("Received empty multipart response from Varna-tz service");
        }

        Map<String, Object> metadata = parseMetadataPart(parts);
        logMetadata(metadata);
        validateExitCode(metadata);

        byte[] svgBytes = extractFileBytes(parts, "clean.svg");
        if (svgBytes == null) {
            throw new VisualizationException("No clean.svg file found in the multipart response");
        }

        String svgContent = new String(svgBytes, StandardCharsets.UTF_8);
        SAXSVGDocumentFactory factory =
                new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
        return factory.createSVGDocument(null, new StringReader(svgContent));
    }

    private SVGDocument handleLegacyResponse(Map<String, Object> response) throws VisualizationException, IOException {
        if (response == null) {
            throw new VisualizationException("Received null response from Varna-tz service");
        }

        logMetadata(response);
        validateExitCode(response);

        if (response.containsKey("output_files") && response.get("output_files") != null) {
            List<Map<String, String>> outputFiles = (List<Map<String, String>>) response.get("output_files");

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
        throw new VisualizationException("No clean.svg file found in the response from Varna-tz service");
    }

    private Map<String, Object> parseMetadataPart(MultiValueMap<String, Object> parts) throws IOException {
        Object metadataPart = parts.getFirst("metadata");
        if (metadataPart == null) {
            metadataPart = findPartByName(parts, "metadata");
        }

        if (metadataPart == null) {
            return Collections.emptyMap();
        }

        if (metadataPart instanceof Map) {
            return (Map<String, Object>) metadataPart;
        }

        byte[] bytes = readPartBytes(metadataPart);
        if (bytes != null) {
            return objectMapper.readValue(bytes, new TypeReference<Map<String, Object>>() {
            });
        }

        if (metadataPart instanceof String) {
            return objectMapper.readValue((String) metadataPart, new TypeReference<Map<String, Object>>() {
            });
        }

        return objectMapper.convertValue(metadataPart, new TypeReference<Map<String, Object>>() {
        });
    }

    private Object findPartByName(MultiValueMap<String, Object> parts, String name) {
        for (Map.Entry<String, List<Object>> entry : parts.entrySet()) {
            for (Object part : entry.getValue()) {
                String partName = getPartName(part);
                if (name.equals(partName)) {
                    return part;
                }
            }
        }
        return null;
    }

    private void logMetadata(Map<String, Object> metadata) {
        if (metadata == null) {
            return;
        }

        if (metadata.containsKey("stdout")) {
            logger.debug("Varna-tz stdout: {}", metadata.get("stdout"));
        }

        if (metadata.containsKey("stderr")) {
            String stderr = String.valueOf(metadata.get("stderr"));
            if (stderr != null && !stderr.isEmpty() && !"null".equals(stderr)) {
                logger.warn("Varna-tz stderr: {}", stderr);
            }
        }
    }

    private void validateExitCode(Map<String, Object> metadata) throws VisualizationException {
        if (metadata == null || !metadata.containsKey("exit_code")) {
            return;
        }

        Integer exitCode = null;
        Object exitValue = metadata.get("exit_code");
        if (exitValue instanceof Number) {
            exitCode = ((Number) exitValue).intValue();
        } else if (exitValue != null) {
            try {
                exitCode = Integer.parseInt(exitValue.toString());
            } catch (NumberFormatException ignored) {
                logger.warn("Unable to parse Varna-tz exit_code: {}", exitValue);
            }
        }

        if (exitCode != null && exitCode != 0) {
            logger.error("Varna-tz command failed with exit code: {}", exitCode);
            throw new VisualizationException("Varna-tz command failed with exit code: " + exitCode);
        }
    }

    private byte[] extractFileBytes(MultiValueMap<String, Object> parts, String expectedFilename)
            throws IOException {
        for (Map.Entry<String, List<Object>> entry : parts.entrySet()) {
            for (Object part : entry.getValue()) {
                String filename = getFilename(part);
                if (filename == null && expectedFilename.equals(entry.getKey())) {
                    filename = entry.getKey();
                }

                if (expectedFilename.equals(filename)) {
                    return readPartBytes(part);
                }
            }
        }
        return null;
    }

    private String getFilename(Object part) {
        if (part instanceof HttpEntity) {
            HttpHeaders headers = ((HttpEntity<?>) part).getHeaders();
            ContentDisposition contentDisposition = headers.getContentDisposition();
            if (contentDisposition != null) {
                return contentDisposition.getFilename();
            }
        }

        if (part instanceof Resource) {
            return ((Resource) part).getFilename();
        }

        return null;
    }

    private String getPartName(Object part) {
        if (part instanceof HttpEntity) {
            HttpHeaders headers = ((HttpEntity<?>) part).getHeaders();
            ContentDisposition contentDisposition = headers.getContentDisposition();
            if (contentDisposition != null) {
                return contentDisposition.getName();
            }
        }
        return null;
    }

    private byte[] readPartBytes(Object part) throws IOException {
        if (part instanceof HttpEntity) {
            Object body = ((HttpEntity<?>) part).getBody();
            return readPartBytes(body);
        }

        if (part instanceof byte[]) {
            return (byte[]) part;
        }

        if (part instanceof String) {
            return ((String) part).getBytes(StandardCharsets.UTF_8);
        }

        if (part instanceof Resource) {
            Resource resource = (Resource) part;
            return resource.getInputStream().readAllBytes();
        }

        return null;
    }
}
