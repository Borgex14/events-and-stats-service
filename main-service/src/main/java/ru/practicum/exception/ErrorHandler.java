package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        log.error("Validation error: {}", e.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse("Validation error", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.error("Not found: {}", e.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse("Not found", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException e) {
        log.error("Conflict: {}", e.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse("Conflict", e.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalError(Exception e) {
        log.error("Internal server error: {}", e.getMessage(), e);
        return new ResponseEntity<>(
                new ErrorResponse("Internal server error", "Произошла непредвиденная ошибка"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}