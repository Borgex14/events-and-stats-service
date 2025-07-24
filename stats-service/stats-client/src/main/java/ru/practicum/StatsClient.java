package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.EndpointHitDtoRequest;
import ru.StatDtoResponse;
import ru.practicum.exception.InternalErrorException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplate rest) {
        this.restTemplate = rest;
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<List<StatDtoResponse>> getStats(LocalDateTime start, LocalDateTime end,
                                                          List<String> uris, Boolean unique) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl)
                    .path("/stats")
                    .queryParam("start", URLEncoder.encode(start.toString(), StandardCharsets.UTF_8))
                    .queryParam("end", URLEncoder.encode(end.toString(), StandardCharsets.UTF_8));

            if (uris != null && !uris.isEmpty()) {
                builder.queryParam("uris", String.join(",", uris));
            }

            if (unique != null) {
                builder.queryParam("unique", unique);
            }

            return restTemplate.exchange(
                    builder.build().toUri(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
        } catch (Exception e) {
            throw new InternalErrorException("Error getting stats: " + e.getMessage());
        }
    }

    public void hit(EndpointHitDtoRequest dto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EndpointHitDtoRequest> entity = new HttpEntity<>(dto, headers);

            restTemplate.exchange(
                    serverUrl + "/hit",
                    HttpMethod.POST,
                    entity,
                    Void.class
            );
        } catch (Exception e) {
            throw new InternalErrorException("Error saving hit: " + e.getMessage());
        }
    }
}