package com.example.web_summaryy.service.impl;

import com.example.web_summaryy.dto.position.PositionDtoResponse;
import com.example.web_summaryy.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ApiServiceImpl implements ApiService {

    private final RestTemplate restTemplate;

    @Value("${url}")
    private String baseUrl;

    @Value("${access}")
    private String accessKey;

    public ApiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PositionDtoResponse[] fetchPositions() {
        if (baseUrl == null || baseUrl.isBlank()) {
            log.warn("[api_service] Свойство url пустое — синхронизация позиций по API пропущена");
            return new PositionDtoResponse[0];
        }

        final String requestUrl = baseUrl + (accessKey != null ? accessKey : "");
        final String safeUrl = baseUrl + "****";

        long started = System.nanoTime();
        log.info("[api_service] Starting fetch: url={}, output=API_CALL", safeUrl);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>("{}", headers);

            ResponseEntity<PositionDtoResponse[]> resp = restTemplate.postForEntity(requestUrl, entity, PositionDtoResponse[].class);

            long tookMs = (System.nanoTime() - started) / 1_000_000L;
            HttpStatusCode status = resp.getStatusCode();

            if (!status.is2xxSuccessful()) {
                String bodyPreview = safeFragment(resp.hasBody() ? String.valueOf(resp.getBody()) : null);
                log.error("[api_service] HTTP {}: url={}, tookMs={}, responseBody={}",
                        status.value(), safeUrl, tookMs, bodyPreview);
                throw new RuntimeException("API returned non-2xx status: " + status);
            }

            PositionDtoResponse[] body = resp.getBody();
            int items = (body == null) ? 0 : body.length;

            if (items == 0) {
                log.warn("[api_service] Empty body received: status={}, url={}, tookMs={}", status.value(), safeUrl, tookMs);
            }

            log.info("[api_service] Success: status={}, receivedItems={}, url={}, tookMs={}",
                    status.value(), items, safeUrl, tookMs);

            return body;

        } catch (HttpStatusCodeException httpEx) {
            long tookMs = (System.nanoTime() - started) / 1_000_000L;
            String bodyPreview = safeFragment(httpEx.getResponseBodyAsString());
            log.error("[api_service] HTTP {}: url={}, tookMs={}, responseBody={}",
                    httpEx.getStatusCode().value(), safeUrl, tookMs, bodyPreview);
            throw httpEx;

        }  catch (Exception ex) {
            long tookMs = (System.nanoTime() - started) / 1_000_000L;
            log.error("[api_service] Unexpected error: url={}, tookMs={}", safeUrl, tookMs, ex);
            throw ex;
        }
    }

    private String safeFragment(String s) {
        if (s == null) return null;
        String trimmed = s.replaceAll("\\s+", " ").trim();
        return trimmed.length() <= 2000 ? trimmed : trimmed.substring(0, 2000) + "...(truncated)";
    }
}
