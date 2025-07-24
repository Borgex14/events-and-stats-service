package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @ResponseStatus(HttpStatus.CREATED)
    public void createHit(@Valid @RequestBody EndpointHitDtoRequest dto) {
        log.info("Creating hit for URI: {}", dto.getUri());
        statsService.create(dto);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatDtoResponse>> getStats(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false) Boolean unique) {

        List<StatDtoResponse> stats = statsService.getStats(start, end, uris, unique);
        return ResponseEntity.ok(stats);
    }
}