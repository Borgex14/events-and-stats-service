package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {
    @Mapping(target = "category", source = "event.category")
    @Mapping(target = "initiator", source = "event.initiator")
    @Mapping(target = "location", source = "event.location")
    @Mapping(target = "confirmedRequests", source = "event.confirmedRequests")
    @Mapping(target = "views", source = "event.views")
    @Mapping(target = "createdOn", source = "event.createdOn")
    @Mapping(target = "publishedOn", source = "event.publishedOn")
    @Mapping(target = "requestModeration", source = "event.requestModeration")
    EventFullDto toFullDto(Event event);

    EventShortDto toShortDto(Event event);

    List<EventShortDto> toShortDto(List<Event> events);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "categoryEntity")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "eventDate", source = "dto.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Event toEntity(NewEventDto dto, Category categoryEntity, User initiator);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventDate", source = "dto.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Event toEntity(EventFullDto dto);

}