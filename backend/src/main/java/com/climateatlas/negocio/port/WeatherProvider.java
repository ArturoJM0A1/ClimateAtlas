package com.climateatlas.negocio.port;

import com.climateatlas.negocio.model.City;
import java.time.LocalDateTime;

/** Puerto de salida para obtener el clima actual de una ciudad. */
public interface WeatherProvider {
    /**
     * Solicita al proveedor una medición actual para la ciudad indicada.
     * La implementación concreta decide cómo comunicarse con el servicio externo.
     */
    WeatherData fetchCurrentWeather(City city);

    /**
     * Datos meteorológicos normalizados: este tipo no reproduce el JSON del proveedor.
     * @param temperature Temperatura actual en grados Celsius según Open-Meteo.
     * @param humidity Humedad relativa actual como valor porcentual.
     * @param windSpeed Velocidad del viento actual en la unidad declarada por el proveedor.
     * @param measuredAt Fecha y hora de la observación devuelta por el proveedor.
     */
    record WeatherData(
            Double temperature, Double humidity, Double windSpeed, LocalDateTime measuredAt
    ) {}
}
