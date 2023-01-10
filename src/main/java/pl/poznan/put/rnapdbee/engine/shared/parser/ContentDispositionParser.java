package pl.poznan.put.rnapdbee.engine.shared.parser;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.shared.exception.ImproperContentDispositionException;

import javax.annotation.Nonnull;
import java.util.Objects;

@Component
public class ContentDispositionParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentDispositionParser.class);

    /**
     * returns filename from Content-Disposition header as defined in RFC 6266.
     * If header is improper, throws an exception.
     *
     * @param contentDispositionHeader header
     * @return parsed Content-Disposition header
     * @throws ImproperContentDispositionException if header is improper or does not contain the filename
     */
    @Nonnull
    public String parseContentDispositionHeader(String contentDispositionHeader) {
        try {
            ContentDisposition contentDisposition = ContentDisposition.parse(contentDispositionHeader);
            return Objects.requireNonNull(contentDisposition.getFilename());
        } catch (IllegalArgumentException | NullPointerException exception) {
            LOGGER.warn(String.format("Exception thrown when parsing content disposition header: %s",
                    exception.getMessage()));
            throw new ImproperContentDispositionException();
        }
    }
}
