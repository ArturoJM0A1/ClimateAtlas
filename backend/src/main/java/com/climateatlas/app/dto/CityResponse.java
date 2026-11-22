package com.climateatlas.app.dto;

import com.climateatlas.negocio.model.City;

/**
 * Contrato HTTP de una ciudad y su lectura meteorológica más reciente.
 *  * Separa el formato de la API del modelo que utiliza la capa negocio.
 *  * @param id Identificador persistente de la ciudad.
 *  * @param name Nombre de la ciudad.
 *  * @param state Estado o entidad federativa.
 *  * @param latitude Latitud geográfica.
 *  * @param longitude Longitud geográfica.
 *  * @param enabled Indica si la ciudad participa en las actualizaciones.
 *  * @param currentWeather Última medición disponible; puede ser null si aún no hay datos.
 */
public record CityResponse(
        Long id, String name, String state, Double latitude, Double longitude,
        Boolean enabled, WeatherReadingResponse currentWeather
) {
    /**
     * Crea la respuesta REST copiando los datos del modelo de negocio y asociando, cuando existe, el clima actual preparado por la capa de aplicación.
     */
    public static CityResponse from(City city, WeatherReadingResponse currentWeather) {
        return new CityResponse(city.getId(), city.getName(), city.getState(),
                city.getLatitude(), city.getLongitude(), city.getEnabled(), currentWeather);
    }
}
