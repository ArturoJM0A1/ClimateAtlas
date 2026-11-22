package com.climateatlas.dto;

import com.climateatlas.domain.WeatherReading;
import java.time.LocalDateTime;

/** DTO de una lectura; no expone directamente la entidad Hibernate. */
public record WeatherReadingResponse(
        Long id, Long cityId, Double temperature, Double humidity, Double windSpeed,
        LocalDateTime measuredAt, LocalDateTime queriedAt
) {
    public static WeatherReadingResponse from(WeatherReading reading) {
        return new WeatherReadingResponse(
                reading.getId(), reading.getCity().getId(), reading.getTemperature(),
                reading.getHumidity(), reading.getWindSpeed(), reading.getMeasuredAt(),
                reading.getQueriedAt());
    }
}
