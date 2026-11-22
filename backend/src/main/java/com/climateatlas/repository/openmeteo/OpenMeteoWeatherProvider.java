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

    /**
     * Recibe el RestClient configurado para Open-Meteo. Reutiliza sus tiempos
     * de espera y su URL base en cada consulta.
     */
    public OpenMeteoWeatherProvider(RestClient openMeteoRestClient) {
        this.restClient = openMeteoRestClient;
    }

    /**
     * Construye una petición GET al endpoint forecast usando las coordenadas de
     * la ciudad y solicita temperatura, humedad relativa y viento actuales.
     * Verifica que la respuesta contenga todos los campos y traduce los fallos
     * HTTP o de formato a WeatherProviderException.
     */
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

    /**
     * Estructura mínima del JSON devuelto por el endpoint de Open-Meteo.
     * @param current Objeto anidado con las variables meteorológicas actuales.
     */
    public record OpenMeteoResponse(OpenMeteoCurrent current) {}
    /**
     * Mapeo de las propiedades actuales del JSON externo a tipos Java.
     * Los nombres coinciden con los campos entregados por Open-Meteo.
     * @param time Hora de medición del proveedor.
     * @param temperature_2m Temperatura a dos metros de altura.
     * @param relative_humidity_2m Humedad relativa expresada como entero porcentual.
     * @param wind_speed_10m Velocidad del viento a diez metros de altura.
     */
    public record OpenMeteoCurrent(String time, Double temperature_2m,
                                   Integer relative_humidity_2m, Double wind_speed_10m) {}
}
