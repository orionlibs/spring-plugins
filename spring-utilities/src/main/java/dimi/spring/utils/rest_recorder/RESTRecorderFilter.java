package dimi.spring.utils.rest_recorder;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class RESTRecorderFilter implements Filter
{
    private static final int MAX_BODY_CHARS = 10_000; // truncate long bodies
    private final RESTCallRecorder recorder;


    public RESTRecorderFilter(RESTCallRecorder recorder)
    {
        this.recorder = Objects.requireNonNull(recorder, "recorder");
    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        if(!(req instanceof HttpServletRequest request) || !(res instanceof HttpServletResponse response))
        {
            chain.doFilter(req, res);
            return;
        }
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, 1000000);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        Instant start = Instant.now();
        long startMs = System.currentTimeMillis();
        try
        {
            chain.doFilter(wrappedRequest, wrappedResponse);
        }
        finally
        {
            long duration = System.currentTimeMillis() - startMs;
            String requestBody = getContentString(wrappedRequest.getContentAsByteArray(),
                            wrappedRequest.getCharacterEncoding());
            String responseBody = getContentString(wrappedResponse.getContentAsByteArray(),
                            wrappedResponse.getCharacterEncoding());
            Map<String, List<String>> requestHeaders = headerMap(request);
            Map<String, List<String>> responseHeaders = headerMap(wrappedResponse);
            RESTCall call = new RESTCall(
                            start,
                            request.getMethod(),
                            request.getRequestURI(),
                            request.getQueryString(),
                            requestHeaders,
                            truncate(requestBody),
                            wrappedResponse.getStatus(),
                            responseHeaders,
                            truncate(responseBody),
                            duration,
                            request.getRemoteAddr(),
                            principalName(request)
            );
            recorder.record(call);
            // important: copy response body back to the original response
            wrappedResponse.copyBodyToResponse();
        }
    }


    private static String principalName(HttpServletRequest request)
    {
        if(request.getUserPrincipal() == null)
        {
            return null;
        }
        return request.getUserPrincipal().getName();
    }


    private static Map<String, List<String>> headerMap(HttpServletRequest req)
    {
        Map<String, List<String>> m = new LinkedHashMap<>();
        var names = Collections.list(req.getHeaderNames());
        for(String name : names)
        {
            m.put(name, Collections.list(req.getHeaders(name)));
        }
        return m;
    }


    private static Map<String, List<String>> headerMap(HttpServletResponse resp)
    {
        Map<String, List<String>> m = new LinkedHashMap<>();
        Collection<String> names = resp.getHeaderNames();
        for(String name : names)
        {
            m.put(name, new ArrayList<>(resp.getHeaders(name)));
        }
        return m;
    }


    private static String getContentString(byte[] buf, String charsetName)
    {
        if(buf == null || buf.length == 0)
        {
            return null;
        }
        Charset cs = (charsetName != null) ? Charset.forName(charsetName) : Charset.defaultCharset();
        // Use StreamUtils to avoid constructing large intermediate arrays (but here we already have bytes)
        return new String(buf, cs);
    }


    private static String truncate(String s)
    {
        if(s == null)
        {
            return null;
        }
        if(s.length() <= MAX_BODY_CHARS)
        {
            return s;
        }
        return s.substring(0, MAX_BODY_CHARS) + "...(truncated)";
    }
}
