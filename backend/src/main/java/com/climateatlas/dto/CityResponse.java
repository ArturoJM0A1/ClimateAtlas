package com.climateatlas.dto;

import com.climateatlas.domain.City;

/** Ciudad más su lectura reciente, si ya existe. */
public record CityResponse(
        Long id, String name, String state, Double latitude, Double longitude,
        Boolean enabled, WeatherReadingResponse currentWeather
) {
    public static CityResponse from(City city, WeatherReadingResponse reading) {
        return new CityResponse(city.getId(), city.getName(), city.getState(),
                city.getLatitude(), city.getLongitude(), city.getEnabled(), reading);
    }
}
