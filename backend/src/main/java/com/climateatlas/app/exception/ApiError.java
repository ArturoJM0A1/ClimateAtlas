package com.climateatlas.app.exception;

import java.time.OffsetDateTime;

/**
 * Contrato común para las respuestas de error HTTP.
 *  * @param timestamp Momento en que se construyó la respuesta de error.
 *  * @param status Código numérico HTTP.
 *  * @param error Texto asociado al estado HTTP.
 *  * @param message Explicación del problema que puede mostrarse al consumidor.
 *  * @param path Ruta de la API que originó el error.
 */
public record ApiError(
        OffsetDateTime timestamp, int status, String error, String message, String path
) {}
