package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.EndpointHitDtoRequest;
import ru.StatDtoResponse;
import ru.practicum.exception.InternalErrorException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class StatsClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    @Autowired
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
        log.info("Sending hit to stats-server: {}", dto);
        String uri = UriComponentsBuilder.fromHttpUrl(serverUrl)
                .path("/hit")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHitDtoRequest> entity = new HttpEntity<>(dto, headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.POST, entity, Void.class);
            log.info("Hit response: {}", response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful()) {
                handleErrorResponse(response.getStatusCode());
            }
        } catch (RestClientResponseException e) {
            log.error("Stats service responded with error: Status={}, Body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            handleErrorResponse(e.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to send hit to stats-server", e);
            throw new InternalErrorException("Ошибка при записи события (метод hit)");
        }
    }

    private void handleErrorResponse(HttpStatusCode status) {
        if (status.equals(HttpStatus.NOT_FOUND)) {
            throw new NotFoundException("Сервис статистики не найден (метод hit)");
        } else if (status.equals(HttpStatus.BAD_REQUEST)) {
            throw new ValidationException("Некорректный запрос к сервису статистики (метод hit)");
        } else if (status.is5xxServerError()) {
            throw new InternalErrorException("Ошибка сервиса статистики (метод hit)");
        } else {
            throw new RuntimeException("Неожиданный ответ от сервиса статистики: " + status);
        }
    }
}