package pl.poznan.put.rnapdbee.engine.shared.image.logic.drawer;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

            logger.info("Varna-tz request: {}", structureData);
            logger.debug("Varna-tz request JSON ({} bytes): {}", jsonContent.length(), jsonContent);

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
            logger.info("Sending draw request to Varna-tz service at: {}", url);

            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class);

            int statusCode = responseEntity.getStatusCodeValue();
            int responseSize = responseEntity.getBody() != null ? responseEntity.getBody().length : 0;
            MediaType contentType = responseEntity.getHeaders().getContentType();
            logger.info("Varna-tz response: status={}, contentType={}, bodySize={} bytes",
                    statusCode, contentType, responseSize);

            if (contentType != null && MediaType.MULTIPART_FORM_DATA.includes(contentType)) {
                String boundary = extractBoundary(contentType);
                if (boundary != null) {
                    return handleMultipartResponse(responseEntity.getBody(), boundary);
                }
                throw new VisualizationException("Multipart response missing boundary parameter");
            }

            Map<String, Object> response = parseJsonResponse(responseEntity.getBody());
            return handleLegacyResponse(response);
        } catch (RestClientException e) {
            logger.error("REST error communicating with Varna-tz service: {}", e.getMessage(), e);
            throw new VisualizationException("Error communicating with Varna-tz service", e);
        } catch (VisualizationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during Varna-tz drawing: {}", e.getMessage(), e);
            throw new VisualizationException("Unexpected error during Varna-tz drawing", e);
        }
    }

    private SVGDocument handleMultipartResponse(byte[] responseBody, String boundary)
            throws IOException, VisualizationException {
        if (responseBody == null || responseBody.length == 0) {
            logger.error("Received empty multipart response from Varna-tz service");
            throw new VisualizationException("Received empty multipart response from Varna-tz service");
        }

        List<MultipartPart> parts = parseMultipartParts(responseBody, boundary);
        logger.debug("Parsed {} multipart parts from response", parts.size());
        for (MultipartPart part : parts) {
            logger.debug("  Part: name={}, filename={}, contentSize={} bytes",
                    part.getName(), part.getFilename(),
                    part.getContent() != null ? part.getContent().length : 0);
        }

        Map<String, Object> metadata = parseMetadataPart(parts);
        logMetadata(metadata);
        validateExitCode(metadata);

        byte[] svgBytes = extractFileBytes(parts, "clean.svg");
        if (svgBytes == null) {
            logger.error("No clean.svg file found in multipart response. Available parts: {}",
                    parts.stream()
                            .map(p -> String.format("name=%s, filename=%s", p.getName(), p.getFilename()))
                            .collect(java.util.stream.Collectors.joining("; ")));
            throw new VisualizationException("No clean.svg file found in the multipart response");
        }

        logger.info("Successfully extracted clean.svg ({} bytes) from Varna-tz response", svgBytes.length);
        String svgContent = new String(svgBytes, StandardCharsets.UTF_8);
        logger.debug("SVG content preview (first 500 chars): {}",
                svgContent.substring(0, Math.min(500, svgContent.length())));
        SAXSVGDocumentFactory factory =
                new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
        return factory.createSVGDocument(null, new StringReader(svgContent));
    }

    private SVGDocument handleLegacyResponse(Map<String, Object> response) throws VisualizationException, IOException {
        if (response == null) {
            logger.error("Received null JSON response from Varna-tz service");
            throw new VisualizationException("Received null response from Varna-tz service");
        }

        logger.debug("Legacy JSON response keys: {}", response.keySet());
        logMetadata(response);
        validateExitCode(response);

        if (response.containsKey("output_files") && response.get("output_files") != null) {
            List<Map<String, String>> outputFiles = (List<Map<String, String>>) response.get("output_files");
            logger.debug("Response contains {} output files", outputFiles.size());

            for (Map<String, String> file : outputFiles) {
                logger.debug("  Output file: relative_path={}, has_content={}",
                        file.get("relative_path"), file.containsKey("content_base64"));
                if ("clean.svg".equals(file.get("relative_path"))
                        && file.containsKey("content_base64")) {
                    byte[] decodedData = Base64.getDecoder().decode(file.get("content_base64"));
                    String svgContent = new String(decodedData, StandardCharsets.UTF_8);
                    logger.info("Successfully decoded clean.svg ({} bytes) from legacy Varna-tz response",
                            decodedData.length);

                    SAXSVGDocumentFactory factory =
                            new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
                    return factory.createSVGDocument(null, new StringReader(svgContent));
                }
            }
        }
        logger.error("No clean.svg file found in legacy JSON response. Response keys: {}", response.keySet());
        throw new VisualizationException("No clean.svg file found in the response from Varna-tz service");
    }

    private Map<String, Object> parseMetadataPart(List<MultipartPart> parts) throws IOException {
        for (MultipartPart part : parts) {
            if ("metadata".equals(part.getName())) {
                return objectMapper.readValue(part.getContent(), new TypeReference<Map<String, Object>>() {
                });
            }
        }

        return null;
    }

    private Map<String, Object> parseJsonResponse(byte[] responseBody) throws IOException {
        if (responseBody == null || responseBody.length == 0) {
            return null;
        }

        return objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {
        });
    }

    private void logMetadata(Map<String, Object> metadata) {
        if (metadata == null) {
            logger.warn("Varna-tz response metadata is null");
            return;
        }

        if (metadata.containsKey("exit_code")) {
            logger.info("Varna-tz exit_code: {}", metadata.get("exit_code"));
        }

        if (metadata.containsKey("stdout")) {
            String stdout = String.valueOf(metadata.get("stdout"));
            if (stdout != null && !stdout.isEmpty() && !"null".equals(stdout)) {
                logger.info("Varna-tz stdout: {}", stdout);
            }
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

    private byte[] extractFileBytes(List<MultipartPart> parts, String expectedFilename) {
        for (MultipartPart part : parts) {
            if (expectedFilename.equals(part.getFilename())) {
                return part.getContent();
            }
        }
        return null;
    }

    private List<MultipartPart> parseMultipartParts(byte[] responseBody, String boundary)
            throws VisualizationException {
        String rawBody = new String(responseBody, StandardCharsets.ISO_8859_1);
        String boundaryMarker = "--" + boundary;
        String[] sections = rawBody.split(Pattern.quote(boundaryMarker));

        List<MultipartPart> parts = new java.util.ArrayList<>();
        for (String section : sections) {
            String trimmed = trimSection(section);
            if (trimmed.isEmpty() || "--".equals(trimmed)) {
                continue;
            }

            int headerEndIndex = trimmed.indexOf("\r\n\r\n");
            if (headerEndIndex < 0) {
                continue;
            }

            String headerBlock = trimmed.substring(0, headerEndIndex);
            String bodyBlock = trimmed.substring(headerEndIndex + 4);
            if (bodyBlock.endsWith("\r\n")) {
                bodyBlock = bodyBlock.substring(0, bodyBlock.length() - 2);
            }

            MultipartHeaders headers = parseHeaders(headerBlock);
            byte[] content = bodyBlock.getBytes(StandardCharsets.ISO_8859_1);
            parts.add(new MultipartPart(headers.name, headers.filename, content));
        }

        if (parts.isEmpty()) {
            throw new VisualizationException("No parts parsed from multipart response");
        }
        return parts;
    }

    private String trimSection(String section) {
        String trimmed = section;
        if (trimmed.startsWith("\r\n")) {
            trimmed = trimmed.substring(2);
        }
        if (trimmed.endsWith("--")) {
            trimmed = trimmed.substring(0, trimmed.length() - 2);
        }
        return trimmed;
    }

    private MultipartHeaders parseHeaders(String headerBlock) {
        String[] lines = headerBlock.split("\r\n");
        String name = null;
        String filename = null;
        for (String line : lines) {
            int colonIndex = line.indexOf(':');
            if (colonIndex < 0) {
                continue;
            }
            String headerName = line.substring(0, colonIndex).trim();
            String headerValue = line.substring(colonIndex + 1).trim();
            if ("Content-Disposition".equalsIgnoreCase(headerName)) {
                name = extractDispositionValue(headerValue, "name");
                filename = extractDispositionValue(headerValue, "filename");
            }
        }
        return new MultipartHeaders(name, filename);
    }

    private String extractDispositionValue(String headerValue, String attribute) {
        Pattern pattern = Pattern.compile(attribute + "=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(headerValue);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractBoundary(MediaType contentType) {
        String boundary = contentType.getParameter("boundary");
        if (boundary == null) {
            return null;
        }
        if (boundary.startsWith("\"") && boundary.endsWith("\"") && boundary.length() > 1) {
            return boundary.substring(1, boundary.length() - 1);
        }
        return boundary;
    }

    private static final class MultipartHeaders {
        private final String name;
        private final String filename;

        private MultipartHeaders(String name, String filename) {
            this.name = name;
            this.filename = filename;
        }
    }

    private static final class MultipartPart {
        private final String name;
        private final String filename;
        private final byte[] content;

        private MultipartPart(String name, String filename, byte[] content) {
            this.name = name;
            this.filename = filename;
            this.content = content;
        }

        private String getName() {
            return name;
        }

        private String getFilename() {
            return filename;
        }

        private byte[] getContent() {
            return content;
        }
    }
}
