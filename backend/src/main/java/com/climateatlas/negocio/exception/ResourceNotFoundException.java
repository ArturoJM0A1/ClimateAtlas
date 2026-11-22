package com.climateatlas.negocio.exception;

/** El recurso solicitado no existe en el dominio. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
