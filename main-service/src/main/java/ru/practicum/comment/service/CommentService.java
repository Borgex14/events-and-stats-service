package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    CommentDtoResponse create(Long userId, Long eventId, CommentDtoRequest dto);

    CommentDtoResponse update(Long userId, Long eventId, Long commId, CommentDtoRequest dto);

    void delete(Long userId, Long eventId, Long commId);

    void deleteCommentAdmin(Long commId);

    CommentDtoResponse getCommentById(Long commentId);

    List<CommentDtoResponse> getCommentsByEventId(Long eventId);

    List<CommentDtoResponse> getCommentsByUserIdAndEventId(Long userId, Long eventId);

    List<CommentDtoResponse> getCommentsAdmin(List<Integer> users, List<Integer> events, LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd, Integer from, Integer size);

}