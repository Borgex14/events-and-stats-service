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
    public ResponseEntity<ErrorResponse> handlerValidationException(ValidationException e) {
        return new ResponseEntity<>(new ErrorResponse("Validation exception", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerNotFoundException(NotFoundException e) {
        log.error("NotFoundException handled: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse("Not found exception", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handlerConflictException(ConflictException e) {
        return new ResponseEntity<>(new ErrorResponse("Conflict exception", e.getMessage()), HttpStatus.CONFLICT);
    }
}