package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.EndpointHitDtoRequest;
import ru.EndpointHitStatsProjection;
import ru.StatDtoResponse;
import ru.practicum.exception.InternalErrorException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EndpointDtoMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.storage.StatsRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
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
        try {
            if (dto == null || dto.getApp() == null || dto.getUri() == null || dto.getIp() == null) {
                throw new ValidationException("Invalid hit data");
            }

            String normalizedUri = normalizeUri(dto.getUri());
            EndpointHit hit = EndpointDtoMapper.mapDtoToEntity(dto);
            hit.setUri(normalizedUri);
            hit.setTimestamp(LocalDateTime.now());
            statsRepository.save(hit);
        } catch (Exception e) {
            log.error("Error saving hit", e);
            throw new InternalErrorException("Error saving hit: " + e.getMessage());
        }
    }

    @Override
    public List<StatDtoResponse> getStats(LocalDateTime start, LocalDateTime end,
                                          List<String> uris, Boolean unique) {
        try {
            log.info("Getting stats from {} to {}, uris: {}, unique: {}", start, end, uris, unique);

            if (start == null || end == null) {
                throw new ValidationException("Start and end dates must be specified");
            }

            if (start.isAfter(end)) {
                throw new ValidationException("Start date must be before end date");
            }

            List<EndpointHitStatsProjection> stats;

            if (uris == null || uris.isEmpty()) {
                stats = Boolean.TRUE.equals(unique) ?
                        statsRepository.findAllNotUrisTrueUnique(start, end) :
                        statsRepository.findAllNotUrisFalseUnique(start, end);
            } else {
                List<String> normalizedUris = uris.stream()
                        .map(this::normalizeUri)
                        .filter(uri -> uri != null && !uri.isEmpty())
                        .collect(Collectors.toList());

                if (normalizedUris.isEmpty()) {
                    return List.of();
                }

                stats = Boolean.TRUE.equals(unique) ?
                        statsRepository.findAllWithUrisTrueUnique(start, end, normalizedUris) :
                        statsRepository.findAllWithUrisFalseUnique(start, end, normalizedUris);
            }

            return stats.stream()
                    .map(projection -> new StatDtoResponse(
                            projection.getApp(),
                            projection.getUri(),
                            projection.getHits()))
                    .sorted(Comparator.comparingLong(StatDtoResponse::getHits).reversed())
                    .collect(Collectors.toList());
        } catch (ValidationException e) {
            throw e; // Re-throw validation exceptions
        } catch (Exception e) {
            log.error("Error getting stats", e);
            throw new InternalErrorException("Error getting stats: " + e.getMessage());
        }
    }

    private String normalizeUri(String uri) {
        if (uri == null || uri.isEmpty()) {
            return uri;
        }

        if ("/events".equals(uri)) {
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
                    return uri;
                }
            }
        }
        return uri;
    }
}