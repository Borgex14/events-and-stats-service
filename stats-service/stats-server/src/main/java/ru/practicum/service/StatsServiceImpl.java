package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.EndpointHitDtoRequest;
import ru.EndpointHitStatsProjection;
import ru.StatDtoResponse;
import ru.practicum.mapper.EndpointDtoMapper;
import ru.practicum.storage.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public void create(EndpointHitDtoRequest dto) {
        log.debug("Saving hit: {}", dto);
        statsRepository.save(EndpointDtoMapper.mapDtoToEntity(dto));
    }

    public List<StatDtoResponse> getStats(LocalDateTime start, LocalDateTime end,
                                          List<String> uris, Boolean unique) {
        validateTime(start, end);

        String defaultUri = "/events";
        if (uris == null || uris.isEmpty()) {
            uris = Collections.singletonList(defaultUri);
        }

        List<EndpointHitStatsProjection> results = unique ?
                statsRepository.findAllWithUrisTrueUnique(start, end, uris) :
                statsRepository.findAllWithUrisTrue(start, end, uris);

        log.debug("Found {} results for uris: {}", results.size(), uris);

        if (results.isEmpty()) {
            return Collections.singletonList(
                    new StatDtoResponse("ewm-main-service", defaultUri, 0L)
            );
        }

        return results.stream()
                .map(proj -> new StatDtoResponse(proj.getApp(), proj.getUri(), proj.getHits()))
                .collect(Collectors.toList());
    }

    private StatDtoResponse mapProjectionToDto(EndpointHitStatsProjection projection) {
        return new StatDtoResponse(
                projection.getApp(),
                projection.getUri(),
                projection.getHits()
        );
    }

    private void validateTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }
}