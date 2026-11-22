package com.climateatlas.negocio.exception;

/** El proveedor meteorológico no entregó una lectura utilizable. */
public class WeatherProviderException extends RuntimeException {
    /**
     * Construye una excepción con un mensaje descriptivo cuando no hay datos meteorológicos utilizables.
     */
    public WeatherProviderException(String message) { super(message); }
    /**
     * Constructor alternativo que conserva la causa original para facilitar el diagnóstico del fallo.
     */
    public WeatherProviderException(String message, Throwable cause) { super(message, cause); }
}
