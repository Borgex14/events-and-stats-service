package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.EndpointHitDtoRequest;
import ru.StatDtoResponse;
import ru.practicum.mapper.EndpointDtoMapper;
import ru.practicum.storage.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Transactional
    public void create(EndpointHitDtoRequest dto) {
        log.debug("Saving hit: {}", dto);
        statsRepository.save(EndpointDtoMapper.mapDtoToEntity(dto));
    }

    public List<StatDtoResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        validateTime(start, end);

        List<Object[]> results;
        if (uris == null || uris.isEmpty()) {
            results = unique ?
                    statsRepository.findAllWithUrisFalseUnique(start, end) :
                    statsRepository.findAllWithUrisFalse(start, end);
        } else {
            results = unique ?
                    statsRepository.findAllWithUrisTrueUnique(start, end, uris) :
                    statsRepository.findAllWithUrisTrue(start, end, uris);
        }

        log.debug("Found {} results", results.size());

        if (results.isEmpty()) {
            return List.of(
                    new StatDtoResponse("ewm-main-service",
                            uris != null && !uris.isEmpty() ? uris.getFirst() : "/events",
                            0L)
            );
        }

        return results.stream()
                .map(this::mapToStatDtoResponse)
                .collect(Collectors.toList());
    }

    private StatDtoResponse mapToStatDtoResponse(Object[] result) {
        if (result == null || result.length < 3) {
            throw new IllegalStateException("Invalid query result format");
        }
        return new StatDtoResponse(
                (String) result[0],
                (String) result[1],
                ((Number) result[2]).longValue()
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