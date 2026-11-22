package com.climateatlas.negocio.exception;

/**
 * Excepción de negocio para indicar que un recurso solicitado no existe.
 *  * La capa app la traduce normalmente a HTTP 404.
 *  *
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Construye la excepción con un mensaje que identifica el recurso ausente; RuntimeException permite propagarla sin declararla en cada firma.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
