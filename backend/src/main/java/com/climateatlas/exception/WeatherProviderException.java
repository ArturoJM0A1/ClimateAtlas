package com.climateatlas.exception;

/** Error de conexión, respuesta HTTP o contenido inválido de Open-Meteo. */
public class WeatherProviderException extends RuntimeException {
    public WeatherProviderException(String message) { super(message); }
    public WeatherProviderException(String message, Throwable cause) { super(message, cause); }
}
