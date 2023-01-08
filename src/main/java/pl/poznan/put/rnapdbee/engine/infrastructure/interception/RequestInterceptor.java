package pl.poznan.put.rnapdbee.engine.infrastructure.interception;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestInterceptor.class);
    private static final String FILENAME_NOT_SET_FORMAT =
            "Filename not set in content-disposition header. Attaching filename = %s to the request.";
    private static final String FILENAME_NOT_PARSABLE_FORMAT =
            "Filename non-parsable. Attaching filename = %s to the request.";
    private static final String CONTENT_DISPOSITION_NOT_SET_FORMAT =
            "Content-disposition header not set. Attaching filename = %s to the request.";
    private static final String UNKNOWN_FILENAME = "UNKNOWN";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        MDC.put("RequestId", RandomStringUtils.randomAlphabetic(8).toUpperCase(Locale.ROOT));

        String filename = getFilenameFromRequest(request);
        MDC.put("FileName", filename);
        LOGGER.info(String.format("Request's query string: %s", request.getQueryString()));
        return true;
    }

    private String getFilenameFromRequest(HttpServletRequest request) {
        String contentDispositionString = request.getHeader(HttpHeaders.CONTENT_DISPOSITION);
        String parsedFilename;
        if (contentDispositionString == null) {
            LOGGER.info(String.format(CONTENT_DISPOSITION_NOT_SET_FORMAT, UNKNOWN_FILENAME));
            return UNKNOWN_FILENAME;
        }

        try {
            ContentDisposition contentDisposition = ContentDisposition.parse(contentDispositionString);
            if (contentDisposition.getFilename() == null) {
                LOGGER.info(String.format(FILENAME_NOT_SET_FORMAT, UNKNOWN_FILENAME));
                parsedFilename = UNKNOWN_FILENAME;
            } else {
                parsedFilename = contentDisposition.getFilename();
            }
        } catch (IllegalArgumentException exception) {
            LOGGER.info(String.format(FILENAME_NOT_PARSABLE_FORMAT, UNKNOWN_FILENAME));
            parsedFilename = UNKNOWN_FILENAME;
        }
        return parsedFilename;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler,
                           @Nullable ModelAndView modelAndView) {
        MDC.clear();
    }
}
