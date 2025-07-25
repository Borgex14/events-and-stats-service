package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.EndpointHitDtoRequest;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.Objects;

@UtilityClass
public class EndpointDtoMapper {
    public EndpointHit mapDtoToEntity(EndpointHitDtoRequest dto) {
        if (dto == null) {
            throw new ValidationException("DTO cannot be null");
        }

        return EndpointHit.builder()
                .app(Objects.requireNonNull(dto.getApp(), "App cannot be null"))
                .uri(Objects.requireNonNull(dto.getUri(), "Uri cannot be null"))
                .ip(Objects.requireNonNull(dto.getIp(), "Ip cannot be null"))
                .timestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now())
                .build();
    }
}