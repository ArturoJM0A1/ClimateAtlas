package com.climateatlas.service;

import com.climateatlas.domain.City;
import com.climateatlas.exception.WeatherProviderException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

/**
 * Cliente HTTP para Open-Meteo. Construye los parámetros de consulta,
 * ejecuta el GET y deserializa JSON a records Java con Jackson.
 */
@Component
public class OpenMeteoClient {

    private final RestClient restClient;

    public OpenMeteoClient(RestClient openMeteoRestClient) {
        this.restClient = openMeteoRestClient;
    }

    public WeatherData fetchCurrentWeather(City city) {
        try {
            OpenMeteoResponse response = restClient.get()
                    .uri(builder -> builder.path("/v1/forecast")
                            .queryParam("latitude", city.getLatitude())
                            .queryParam("longitude", city.getLongitude())
                            .queryParam("current",
                                    "temperature_2m,relative_humidity_2m,wind_speed_10m")
                            .queryParam("timezone", "auto")
                            .build())
                    .retrieve()
                    .body(OpenMeteoResponse.class);

            if (response == null || response.current() == null) {
                throw new WeatherProviderException(
                        "Open-Meteo devolvió una respuesta sin datos actuales.");
            }

            OpenMeteoCurrent current = response.current();
            if (current.time() == null || current.temperature_2m() == null
                    || current.relative_humidity_2m() == null
                    || current.wind_speed_10m() == null) {
                throw new WeatherProviderException(
                        "Open-Meteo devolvió campos meteorológicos incompletos.");
            }

            return new WeatherData(current.temperature_2m(),
                    current.relative_humidity_2m().doubleValue(),
                    current.wind_speed_10m(), LocalDateTime.parse(current.time()));
        } catch (WeatherProviderException exception) {
            throw exception;
        } catch (RestClientException | IllegalArgumentException exception) {
            throw new WeatherProviderException(
                    "No fue posible consultar Open-Meteo para " + city.getName() + ".", exception);
        }
    }

    // Los nombres de los componentes coinciden con las propiedades JSON del proveedor.
    public record OpenMeteoResponse(OpenMeteoCurrent current) {}
    public record OpenMeteoCurrent(
            String time, Double temperature_2m,
            Integer relative_humidity_2m, Double wind_speed_10m) {}
    public record WeatherData(
            Double temperature, Double humidity, Double windSpeed, LocalDateTime measuredAt) {}
}
