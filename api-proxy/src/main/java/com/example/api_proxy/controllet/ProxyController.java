package com.example.api_proxy.controllet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
public class ProxyController {

    // Headers that must not be copied between connections
    private static final Set<String> SKIP_REQUEST_HEADERS = Set.of(
            "host", "connection", "content-length", "transfer-encoding", "accept-encoding",
            "upgrade", "keep-alive", "te", "trailer", "proxy-authorization", "proxy-authenticate");

    private static final Set<String> SKIP_RESPONSE_HEADERS = Set.of(
            "connection", "content-length", "transfer-encoding",
            "keep-alive", "upgrade", "te", "trailer", "proxy-authenticate");

    private final RestTemplate restTemplate;
    private final String targetUrl;

    public ProxyController(RestTemplate restTemplate,
                           @Value("${proxy.target-url}") String targetUrl) {
        this.restTemplate = restTemplate;
        this.targetUrl = targetUrl.endsWith("/") ? targetUrl.substring(0, targetUrl.length() - 1) : targetUrl;
    }

    /** Catches every URL, every HTTP method. */
    @RequestMapping("/**")
    public ResponseEntity<byte[]> forward(HttpServletRequest request) throws IOException {

        // 1. Build the target URL: same path + same query string, different host/port
        String query = request.getQueryString();
        URI target = URI.create(targetUrl + request.getRequestURI() + (query == null ? "" : "?" + query));

        // 2. Copy request headers
        HttpHeaders headers = new HttpHeaders();
        for (String name : Collections.list(request.getHeaderNames())) {
            if (!SKIP_REQUEST_HEADERS.contains(name.toLowerCase())) {
                headers.addAll(name, Collections.list(request.getHeaders(name)));
            }
        }

        // 3. Copy request body (JSON, form data, etc.)
        byte[] body = request.getInputStream().readAllBytes();
        HttpEntity<byte[]> entity = new HttpEntity<>(body.length == 0 ? null : body, headers);

        // 4. Call localhost:8080
        ResponseEntity<byte[]> response;
        try {
            response = restTemplate.exchange(target, HttpMethod.valueOf(request.getMethod()), entity, byte[].class);
        } catch (ResourceAccessException e) {
            String json = "{\"error\":\"Target service " + targetUrl + " is not reachable\"}";
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json.getBytes());
        }

        // 5. Copy response headers; redirects must point back to the proxy (8081), not to 8080
        String proxyBase = request.getScheme() + "://" + request.getHeader("Host");
        HttpHeaders out = new HttpHeaders();
        response.getHeaders().forEach((name, values) -> {
            if (SKIP_RESPONSE_HEADERS.contains(name.toLowerCase())) {
                return;
            }
            if (HttpHeaders.LOCATION.equalsIgnoreCase(name)) {
                List<String> rewritten = values.stream()
                        .map(v -> v.startsWith(targetUrl) ? proxyBase + v.substring(targetUrl.length()) : v)
                        .toList();
                out.put(name, rewritten);
            } else {
                out.put(name, values);
            }
        });

        // 6. Return the same status, headers and body to the caller
        return ResponseEntity.status(response.getStatusCode()).headers(out).body(response.getBody());
    }
}
