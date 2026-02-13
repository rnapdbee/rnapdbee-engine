package pl.poznan.put.rnapdbee.engine.shared.multiplet.boundary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

@Service
public class CoplanarityClient {
    private static final Logger logger = LoggerFactory.getLogger(CoplanarityClient.class);
    private static final String RUN_COMMAND_PATH = "/run-command";

    private final RestTemplate restTemplate;
    private final String serviceUrl;
    private final ObjectMapper objectMapper;

    public CoplanarityClient(@Value("${coplanarity.service.url}") String serviceUrl) {
        this.restTemplate = new RestTemplate();
        this.serviceUrl = serviceUrl;
        this.objectMapper = new ObjectMapper();
        logger.info("CoplanarityClient initialized with service URL: {}", serviceUrl);
    }

    public Boolean areBasesCoplanar(String cifContent) {
        Map<String, Boolean> results = areBasesCoplanar(Map.of("input.cif", cifContent));
        if (results == null) {
            return null;
        }
        return results.get("input.cif");
    }

    public Map<String, Boolean> areBasesCoplanar(Map<String, String> cifContents) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("arguments", "coplanarity-checker-wrapper.py");
            body.add("output_files", "output.json");

            for (Map.Entry<String, String> entry : cifContents.entrySet()) {
                ByteArrayResource fileResource =
                        new ByteArrayResource(entry.getValue().getBytes(StandardCharsets.UTF_8)) {
                            @Override
                            public String getFilename() {
                                return entry.getKey();
                            }
                        };
                body.add("input_files", fileResource);
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String url = UriComponentsBuilder.fromHttpUrl(serviceUrl)
                    .path(RUN_COMMAND_PATH)
                    .toUriString();
            logger.debug("Sending batch coplanarity request to CLI2REST at: {} with {} files", url, cifContents.size());

            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class);

            MediaType contentType = responseEntity.getHeaders().getContentType();
            if (contentType == null || !MediaType.MULTIPART_FORM_DATA.includes(contentType)) {
                logger.warn("Coplanarity response is not multipart");
                return null;
            }

            String boundary = extractBoundary(contentType);
            if (boundary == null) {
                logger.warn("Coplanarity response boundary missing");
                return null;
            }

            List<MultipartPart> parts = parseMultipartParts(responseEntity.getBody(), boundary);

            Map<String, Object> metadata = parseMetadataPart(parts);
            if (metadata != null) {
                logMetadata(metadata);
                Integer exitCode = parseExitCode(metadata);
                if (exitCode != null && exitCode != 0) {
                    logger.warn("Coplanarity command failed with exit code: {}", exitCode);
                    return null;
                }
            }

            for (MultipartPart part : parts) {
                if ("output.json".equals(part.getFilename())) {
                    return objectMapper.readValue(part.getContent(), new TypeReference<Map<String, Boolean>>() {});
                }
            }

            logger.warn("Coplanarity response missing output.json part");
            return null;
        } catch (RestClientException e) {
            logger.warn("Error communicating with coplanarity service", e);
            return null;
        } catch (Exception e) {
            logger.warn("Unexpected error during batch coplanarity check", e);
            return null;
        }
    }

    private Map<String, Object> parseMetadataPart(List<MultipartPart> parts) throws Exception {
        for (MultipartPart part : parts) {
            if ("metadata".equals(part.getName())) {
                return objectMapper.readValue(part.getContent(), new TypeReference<Map<String, Object>>() {
                });
            }
        }
        return null;
    }

    private void logMetadata(Map<String, Object> metadata) {
        if (metadata.containsKey("stdout")) {
            logger.debug("Coplanarity stdout: {}", metadata.get("stdout"));
        }
        if (metadata.containsKey("stderr")) {
            String stderr = String.valueOf(metadata.get("stderr"));
            if (stderr != null && !stderr.isEmpty() && !"null".equals(stderr)) {
                logger.warn("Coplanarity stderr: {}", stderr);
            }
        }
    }

    private Integer parseExitCode(Map<String, Object> metadata) {
        if (!metadata.containsKey("exit_code")) {
            return null;
        }
        Object exitValue = metadata.get("exit_code");
        if (exitValue instanceof Number) {
            return ((Number) exitValue).intValue();
        }
        if (exitValue != null) {
            try {
                return Integer.parseInt(exitValue.toString());
            } catch (NumberFormatException ignored) {
                logger.warn("Unable to parse coplanarity exit_code: {}", exitValue);
            }
        }
        return null;
    }

    private List<MultipartPart> parseMultipartParts(byte[] responseBody, String boundary) {
        if (responseBody == null || responseBody.length == 0) {
            return List.of();
        }
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
