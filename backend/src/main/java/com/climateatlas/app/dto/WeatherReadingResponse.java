package com.climateatlas.app.dto;

import com.climateatlas.negocio.model.WeatherReading;
import java.time.LocalDateTime;

/**
 * Contrato HTTP de salida para una medición meteorológica; no expone entidades JPA.
 *  * @param id Identificador de la medición almacenada.
 *  * @param cityId Identificador de la ciudad asociada.
 *  * @param temperature Temperatura medida en grados Celsius.
 *  * @param humidity Humedad relativa expresada como porcentaje.
 *  * @param windSpeed Velocidad del viento según la unidad entregada por el proveedor.
 *  * @param measuredAt Momento al que corresponden los datos meteorológicos.
 *  * @param queriedAt Momento en que ClimateAtlas consultó al proveedor.
 */
public record WeatherReadingResponse(
        Long id, Long cityId, Double temperature, Double humidity, Double windSpeed,
        LocalDateTime measuredAt, LocalDateTime queriedAt
) {
    /**
     * Crea el DTO de salida a partir del modelo de negocio y obtiene el identificador de ciudad desde la relación City.
     */
    public static WeatherReadingResponse from(WeatherReading reading) {
        return new WeatherReadingResponse(reading.getId(), reading.getCity().getId(),
                reading.getTemperature(), reading.getHumidity(), reading.getWindSpeed(),
                reading.getMeasuredAt(), reading.getQueriedAt());
    }
}
