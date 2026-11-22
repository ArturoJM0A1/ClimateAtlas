package com.climateatlas.batch;

import com.climateatlas.domain.City;
import com.climateatlas.domain.WeatherReading;
import com.climateatlas.exception.WeatherProviderException;
import com.climateatlas.service.OpenMeteoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.time.LocalDateTime;

/**
 * Convierte City en WeatherReading. En Spring Batch devolver null filtra
 * el elemento: la ciudad con fallo externo no llega al writer, pero las
 * siguientes ciudades se siguen procesando.
 */
public class WeatherItemProcessor implements ItemProcessor<City, WeatherReading> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherItemProcessor.class);
    private final OpenMeteoClient openMeteoClient;

    public WeatherItemProcessor(OpenMeteoClient openMeteoClient) {
        this.openMeteoClient = openMeteoClient;
    }

    @Override
    public WeatherReading process(City city) {
        try {
            OpenMeteoClient.WeatherData weather = openMeteoClient.fetchCurrentWeather(city);
            return new WeatherReading(city, weather.temperature(), weather.humidity(),
                    weather.windSpeed(), weather.measuredAt(), LocalDateTime.now());
        } catch (WeatherProviderException exception) {
            LOGGER.error("Se omitió la ciudad {} (id={}): {}",
                    city.getName(), city.getId(), exception.getMessage());
            return null;
        }
    }
}
