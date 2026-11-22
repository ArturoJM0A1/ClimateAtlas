package com.climateatlas.app.dto;

import com.climateatlas.negocio.model.City;

/** Contrato HTTP de una ciudad y su lectura más reciente. */
public record CityResponse(
        Long id, String name, String state, Double latitude, Double longitude,
        Boolean enabled, WeatherReadingResponse currentWeather
) {
    public static CityResponse from(City city, WeatherReadingResponse currentWeather) {
        return new CityResponse(city.getId(), city.getName(), city.getState(),
                city.getLatitude(), city.getLongitude(), city.getEnabled(), currentWeather);
    }
}
