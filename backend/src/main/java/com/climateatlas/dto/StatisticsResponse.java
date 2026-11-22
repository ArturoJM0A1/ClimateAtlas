package com.climateatlas.dto;

/** Estadísticas derivadas de los registros persistidos, nunca simulados. */
public record StatisticsResponse(
        long totalCities, long enabledCities, long totalReadings,
        Double averageCurrentTemperature
) {}
