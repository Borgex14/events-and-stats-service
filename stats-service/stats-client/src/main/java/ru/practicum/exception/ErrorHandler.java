package ru.practicum.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<Map<String, String>> handleInternalError(InternalErrorException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal Error");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationError(ValidationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Validation Error");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Недостающий параметр");
        response.put("message", String.format("Параметр '%s' обязателен", ex.getParameterName()));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Map<String, String>> handleDateTimeParseException(DateTimeParseException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Invalid date format");
        response.put("message", "Required format: yyyy-MM-dd HH:mm:ss");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> handleDataAccessException(DataAccessException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Database error");
        response.put("message", "Error accessing data");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Invalid argument");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Invalid parameter type");
        response.put("message", "Parameter '" + ex.getName() + "' should be of type " + ex.getRequiredType().getSimpleName());
        return ResponseEntity.badRequest().body(response);
    }
}