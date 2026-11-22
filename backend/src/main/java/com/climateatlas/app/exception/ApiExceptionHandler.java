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
    /**
     * Convierte ResourceNotFoundException en una respuesta HTTP 404 e incluye el mensaje y la ruta solicitada.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException exception, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    /**
     * Convierte los argumentos inválidos detectados por negocio en HTTP 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(
            IllegalArgumentException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    /**
     * Recopila los errores de validación de los campos del DTO, los concatena en un mensaje y devuelve HTTP 400.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return response(HttpStatus.BAD_REQUEST, message, request);
    }

    /**
     * Convierte una violación de integridad de la base de datos —por ejemplo, una restricción UNIQUE— en HTTP 409 Conflict.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict(
            DataIntegrityViolationException exception, HttpServletRequest request) {
        return response(HttpStatus.CONFLICT,
                "La operación viola una restricción de integridad de la base de datos.", request);
    }

    /**
     * Indica HTTP 503 Service Unavailable cuando el proveedor meteorológico o el inicio del proceso Batch no están disponibles.
     */
    @ExceptionHandler(WeatherProviderException.class)
    public ResponseEntity<ApiError> handleProviderFailure(
            WeatherProviderException exception, HttpServletRequest request) {
        return response(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage(), request);
    }

    /**
     * Centraliza la construcción del cuerpo ApiError para que todos los manejadores devuelvan la misma estructura y el estado HTTP correspondiente.
     */
    private ResponseEntity<ApiError> response(
            HttpStatus status, String message, HttpServletRequest request) {
        ApiError body = new ApiError(OffsetDateTime.now(), status.value(),
                status.getReasonPhrase(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
