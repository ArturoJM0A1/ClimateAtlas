package com.climateatlas.app.dto;

import com.climateatlas.negocio.model.WeatherStatistics;

/** DTO de salida para las estadísticas del dashboard. */
public record StatisticsResponse(
        long totalCities, long enabledCities, long totalReadings, Double averageCurrentTemperature
) {
    public static StatisticsResponse from(WeatherStatistics statistics) {
        return new StatisticsResponse(statistics.totalCities(), statistics.enabledCities(),
                statistics.totalReadings(), statistics.averageCurrentTemperature());
    }
}
