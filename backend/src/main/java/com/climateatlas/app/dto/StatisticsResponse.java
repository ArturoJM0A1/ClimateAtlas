package com.climateatlas.app.dto;

import com.climateatlas.negocio.model.WeatherStatistics;

/**
 * DTO de salida para las estadísticas del dashboard.
 *  * @param totalCities Número total de ciudades registradas.
 *  * @param enabledCities Número de ciudades habilitadas para recibir actualizaciones.
 *  * @param totalReadings Cantidad total de mediciones almacenadas.
 *  * @param averageCurrentTemperature Promedio de las últimas temperaturas disponibles; null si no hay mediciones.
 */
public record StatisticsResponse(
        long totalCities, long enabledCities, long totalReadings, Double averageCurrentTemperature
) {
    /**
     * Convierte el resultado de negocio WeatherStatistics al formato que consume el frontend, sin trasladar al DTO la lógica de cálculo.
     */
    public static StatisticsResponse from(WeatherStatistics statistics) {
        return new StatisticsResponse(statistics.totalCities(), statistics.enabledCities(),
                statistics.totalReadings(), statistics.averageCurrentTemperature());
    }
}
