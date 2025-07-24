package ru.practicum.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.EndpointHitDtoRequest;
import ru.EndpointHitStatsProjection;
import ru.StatDtoResponse;
import ru.practicum.mapper.EndpointDtoMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.storage.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void create(EndpointHitDtoRequest dto) {
        String normalizedUri = normalizeUri(dto.getUri());
        EndpointHit hit = EndpointDtoMapper.mapDtoToEntity(dto);
        hit.setUri(normalizedUri);
        hit.setTimestamp(LocalDateTime.now());
        statsRepository.save(hit);
    }

    @Override
    public List<StatDtoResponse> getStats(LocalDateTime start, LocalDateTime end,
                                          List<String> uris, Boolean unique) {

        if (start.isAfter(LocalDateTime.now())) {
            throw new ValidationException("Start date cannot be in the future");
        }

        if (end.isAfter(LocalDateTime.now().plusYears(1))) {
            throw new ValidationException("End date too far in the future");
        }

        log.info("Getting stats for: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        List<EndpointHitStatsProjection> resultList;

        if (uris == null || uris.isEmpty()) {
            resultList = Boolean.TRUE.equals(unique) ?
                    statsRepository.findAllNotUrisTrueUnique(start, end) :
                    statsRepository.findAllNotUrisFalseUnique(start, end);
        } else {
            List<String> searchUris = uris.stream()
                    .map(this::normalizeUri)
                    .collect(Collectors.toList());

            log.info("Searching for normalized URIs: {}", searchUris);

            resultList = Boolean.TRUE.equals(unique) ?
                    statsRepository.findAllWithUrisTrueUnique(start, end, searchUris) :
                    statsRepository.findAllWithUrisFalseUnique(start, end, searchUris);
        }

        return resultList.stream()
                .map(stat -> StatDtoResponse.builder()
                        .app(stat.getApp())
                        .uri(stat.getUri())
                        .hits(stat.getHits())
                        .build())
                .sorted((a, b) -> b.getHits().compareTo(a.getHits()))
                .collect(Collectors.toList());
    }

    private String normalizeUri(String uri) {
        if (uri == null) return null;

        if (uri.equals("/events")) {
            return uri;
        }

        if (uri.startsWith("/events/")) {
            String[] parts = uri.split("/");
            if (parts.length >= 3) {
                String idPart = parts[2];
                try {
                    UUID uuid = UUID.fromString(idPart);
                    int numericId = uuid.hashCode() & Integer.MAX_VALUE;
                    return "/events/" + numericId;
                } catch (IllegalArgumentException e) {
                    if (idPart.matches("\\d+")) {
                        return uri;
                    }
                    throw new ValidationException("Invalid event ID format: " + idPart);
                }
            }
        }

        return uri;
    }
}