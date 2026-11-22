package com.climateatlas.negocio.model;

/** Resultado de negocio de las estadísticas meteorológicas. */
public record WeatherStatistics(
        long totalCities, long enabledCities,
        long totalReadings, Double averageCurrentTemperature
) {}
