package com.climateatlas.negocio.model;

/**
 * Resultado inmutable que agrupa los indicadores calculados por WeatherService.
 *  * @param totalCities Número total de ciudades existentes.
 *  * @param enabledCities Número de ciudades habilitadas.
 *  * @param totalReadings Total de mediciones persistidas.
 *  * @param averageCurrentTemperature Promedio de la última temperatura por ciudad; puede ser null si no hay datos.
 */
public record WeatherStatistics(
        long totalCities, long enabledCities,
        long totalReadings, Double averageCurrentTemperature
) {}
