package com.climateatlas.app.dto;

import com.climateatlas.negocio.model.WeatherReading;
import java.time.LocalDateTime;

/** Contrato HTTP de salida; no expone entidades JPA. */
public record WeatherReadingResponse(
        Long id, Long cityId, Double temperature, Double humidity, Double windSpeed,
        LocalDateTime measuredAt, LocalDateTime queriedAt
) {
    public static WeatherReadingResponse from(WeatherReading reading) {
        return new WeatherReadingResponse(reading.getId(), reading.getCity().getId(),
                reading.getTemperature(), reading.getHumidity(), reading.getWindSpeed(),
                reading.getMeasuredAt(), reading.getQueriedAt());
    }
}
