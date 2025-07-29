package ru.practicum;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.EndpointHitDtoRequest;
import ru.StatDtoResponse;
import ru.practicum.exception.InternalErrorException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplate rest) {
        this.restTemplate = rest;
        this.serverUrl = serverUrl;
    }

    public List<StatDtoResponse> getStats(LocalDateTime start, LocalDateTime end,
                                          List<String> uris, Boolean unique) {
        log.info("Getting stats from {} to {}, uris: {}, unique: {}", start, end, uris, unique);
            String uri = UriComponentsBuilder.fromHttpUrl(serverUrl)
                    .path("/stats")
                    .queryParam("start", formatDateTime(start))
                    .queryParam("end", formatDateTime(end))
                    .queryParam("uris", uris != null ? String.join(",", uris) : "")
                    .queryParam("unique", unique)
                    .toUriString();

            log.debug("Requesting stats from: {}", uri);

            ResponseEntity<List<StatDtoResponse>> response = restTemplate.exchange(
                    uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new NotFoundException("Статистика не найдена");
        } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            throw new ValidationException("Некорректные параметры запроса");
        } else if (response.getStatusCode().is5xxServerError()) {
            throw new InternalErrorException("Ошибка сервера при получении статистики");
        }

        if (response.getBody() == null || response.getBody().isEmpty()) {
            return Collections.emptyList();
        }

            return response.getBody();
    }

    public void hit(EndpointHitDtoRequest dto) {
        String uri = UriComponentsBuilder.fromHttpUrl(serverUrl)
                .path("/hit")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHitDtoRequest> entity = new HttpEntity<>(dto, headers);

        restTemplate.exchange(uri, HttpMethod.POST, entity, Void.class);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}