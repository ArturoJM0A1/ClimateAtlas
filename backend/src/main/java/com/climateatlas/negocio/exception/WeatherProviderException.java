package com.climateatlas.negocio.exception;

/** El proveedor meteorológico no entregó una lectura utilizable. */
public class WeatherProviderException extends RuntimeException {
    public WeatherProviderException(String message) { super(message); }
    public WeatherProviderException(String message, Throwable cause) { super(message, cause); }
}
