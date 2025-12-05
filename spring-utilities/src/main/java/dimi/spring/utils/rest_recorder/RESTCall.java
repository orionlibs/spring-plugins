package dimi.spring.utils.rest_recorder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record RESTCall(Instant timestamp,
                       String method,
                       String path,
                       String queryString,
                       Map<String, List<String>> requestHeaders,
                       String requestBody,
                       int responseStatus,
                       Map<String, List<String>> responseHeaders,
                       String responseBody,
                       long durationMillis,
                       String remoteAddr,
                       String principalName)
{
}
