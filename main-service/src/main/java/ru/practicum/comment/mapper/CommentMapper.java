package ru.practicum.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "user", source = "user")
    @Mapping(target = "event", source = "event")
    CommentDtoResponse toDto(Comment comment);

    List<CommentDtoResponse> toDto(List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", source = "eventEntity")
    @Mapping(target = "user", source = "userEntity")
    @Mapping(target = "created", source = "createdEntity")
    Comment toEntity(CommentDtoRequest dto, Event eventEntity, User userEntity, LocalDateTime createdEntity);

}