package pl.poznan.put.rnapdbee.engine.shared;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Class containing utils methods for encoding/decoding
 */
public class EncodingUtils {

    /**
     * decodes Base64 encoded string to decoded one.
     *
     * @param encodedString String encoded in base64
     * @return decoded string
     */
    public static String decodeBase64ToString(String encodedString) {
        final byte[] base64 = Base64.decodeBase64(encodedString);
        final InflaterInputStream stream =
                new InflaterInputStream(new ByteArrayInputStream(base64), new Inflater(true));
        try {
            return IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
