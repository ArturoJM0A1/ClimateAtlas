package com.climateatlas.negocio.port;

import com.climateatlas.negocio.model.City;
import java.time.LocalDateTime;

/** Puerto de salida para obtener el clima actual de una ciudad. */
public interface WeatherProvider {
    WeatherData fetchCurrentWeather(City city);

    record WeatherData(
            Double temperature, Double humidity, Double windSpeed, LocalDateTime measuredAt
    ) {}
}
