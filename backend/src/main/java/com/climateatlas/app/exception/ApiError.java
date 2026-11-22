package com.climateatlas.app.exception;

import java.time.OffsetDateTime;

/** Contrato común para las respuestas de error HTTP. */
public record ApiError(
        OffsetDateTime timestamp, int status, String error, String message, String path
) {}
