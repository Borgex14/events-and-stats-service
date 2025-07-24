package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.EndpointHitDtoRequest;
import ru.StatDtoResponse;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public void createHit(@Valid @RequestBody EndpointHitDtoRequest dto) {
        log.info("Creating hit for URI: {}", dto.getUri());
        statsService.create(dto);
    }

    @GetMapping("/stats")
    public List<StatDtoResponse> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean unique) {

        log.info("Request stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        return statsService.getStats(start, end, uris, unique);
    }
}