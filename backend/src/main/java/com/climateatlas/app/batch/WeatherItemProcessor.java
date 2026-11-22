package com.climateatlas.app.batch;

import com.climateatlas.negocio.exception.WeatherProviderException;
import com.climateatlas.negocio.model.City;
import com.climateatlas.negocio.model.WeatherReading;
import com.climateatlas.negocio.port.WeatherProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.time.LocalDateTime;

/** Coordina la consulta al proveedor y transforma la ciudad en una lectura. */
public class WeatherItemProcessor implements ItemProcessor<City, WeatherReading> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherItemProcessor.class);
    private final WeatherProvider weatherProvider;

    /**
     * Constructor: recibe el puerto WeatherProvider para consultar datos sin acoplar el procesamiento al cliente HTTP de Open-Meteo.
     */
    public WeatherItemProcessor(WeatherProvider weatherProvider) {
        this.weatherProvider = weatherProvider;
    }

    /**
     * Recibe una ciudad y solicita su clima actual. Con la respuesta construye WeatherReading, registrando cuándo se midió el dato y cuándo se consultó. Si falla el proveedor, registra el error y devuelve null para que Batch omita esa ciudad.
     */
    @Override
    public WeatherReading process(City city) {
        try {
            WeatherProvider.WeatherData weather = weatherProvider.fetchCurrentWeather(city);
            return new WeatherReading(null, city, weather.temperature(), weather.humidity(),
                    weather.windSpeed(), weather.measuredAt(), LocalDateTime.now());
        } catch (WeatherProviderException exception) {
            LOGGER.error("Se omitió la ciudad {} (id={}): {}",
                    city.getName(), city.getId(), exception.getMessage());
            return null;
        }
    }
}
