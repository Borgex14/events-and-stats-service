package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.EndpointHitDtoRequest;
import ru.StatDtoResponse;
import ru.practicum.exception.InternalErrorException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
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
            String uri = UriComponentsBuilder.fromHttpUrl(serverUrl)
                    .path("/stats")
                    .queryParam("start", URLEncoder.encode(start.toString(), StandardCharsets.UTF_8))
                    .queryParam("end", URLEncoder.encode(end.toString(), StandardCharsets.UTF_8))
                    .queryParam("uris", uris != null ? String.join(",", uris) : "")
                    .queryParam("unique", unique)
                    .toUriString();

            ResponseEntity<List<StatDtoResponse>> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("Статистика не найдена");
            } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ValidationException("Некорректные параметры запроса");
            } else if (response.getStatusCode().is5xxServerError()) {
                throw new InternalErrorException("Ошибка сервера при получении статистики");
            }

            if (response.getBody() == null || response.getBody().isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            return response;

        } catch (RestClientException e) {
            throw new InternalErrorException("Ошибка при выполнении запроса к сервису статистики");
        }
    }

    public void hit(EndpointHitDtoRequest dto) {
        String uri = UriComponentsBuilder.fromHttpUrl(serverUrl)
                .path("/hit")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHitDtoRequest> entity = new HttpEntity<>(dto, headers);

        ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.POST, entity, Void.class);

        if (response.getStatusCode().value() == 404) {
            throw new NotFoundException("Ошибка при записи события (метод hit)");

        } else if (response.getStatusCode().value() == 400) {
            throw new ValidationException("Ошибка при записи события(метод hit)");

        } else if (response.getStatusCode().is5xxServerError()) {
            throw new InternalErrorException("Ошибка при записи события(метод hit)");
        }
    }
}