package com.climateatlas.exception;

/** Se utiliza cuando un recurso no existe para el identificador recibido. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
