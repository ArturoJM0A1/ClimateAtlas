package com.climateatlas.exception;

import java.time.OffsetDateTime;

/** Estructura común de las respuestas de error. */
public record ApiError(OffsetDateTime timestamp, int status, String error,
                       String message, String path) {}
