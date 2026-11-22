package com.climateatlas.repository.openmeteo;

import com.climateatlas.negocio.exception.WeatherProviderException;
import com.climateatlas.negocio.model.City;
import com.climateatlas.negocio.port.WeatherProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.time.LocalDateTime;

/**
 * Adaptador de Open-Meteo. Implementa el puerto definido en negocio y contiene
 * los detalles externos: HTTP, endpoint y estructura JSON del proveedor.
 */
@Component
public class OpenMeteoWeatherProvider implements WeatherProvider {
    private final RestClient restClient;

    public OpenMeteoWeatherProvider(RestClient openMeteoRestClient) {
        this.restClient = openMeteoRestClient;
    }

    @Override
    public WeatherData fetchCurrentWeather(City city) {
        try {
            OpenMeteoResponse response = restClient.get()
                    .uri(builder -> builder.path("/v1/forecast")
                            .queryParam("latitude", city.getLatitude())
                            .queryParam("longitude", city.getLongitude())
                            .queryParam("current",
                                    "temperature_2m,relative_humidity_2m,wind_speed_10m")
                            .queryParam("timezone", "auto").build())
                    .retrieve().body(OpenMeteoResponse.class);

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
                    "No fue posible consultar Open-Meteo para " + city.getName(), exception);
        }
    }

    public record OpenMeteoResponse(OpenMeteoCurrent current) {}
    public record OpenMeteoCurrent(String time, Double temperature_2m,
                                   Integer relative_humidity_2m, Double wind_speed_10m) {}
}
