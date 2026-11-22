package com.climateatlas.app.exception;

import com.climateatlas.negocio.exception.ResourceNotFoundException;
import com.climateatlas.negocio.exception.WeatherProviderException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

/** Traduce excepciones de negocio y persistencia a estados HTTP. */
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException exception, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(
            IllegalArgumentException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return response(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict(
            DataIntegrityViolationException exception, HttpServletRequest request) {
        return response(HttpStatus.CONFLICT,
                "La operación viola una restricción de integridad de la base de datos.", request);
    }

    @ExceptionHandler(WeatherProviderException.class)
    public ResponseEntity<ApiError> handleProviderFailure(
            WeatherProviderException exception, HttpServletRequest request) {
        return response(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage(), request);
    }

    private ResponseEntity<ApiError> response(
            HttpStatus status, String message, HttpServletRequest request) {
        ApiError body = new ApiError(OffsetDateTime.now(), status.value(),
                status.getReasonPhrase(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
