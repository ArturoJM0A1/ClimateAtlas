package com.climateatlas.app.application;

import com.climateatlas.app.dto.CityRequest;
import com.climateatlas.app.dto.CityResponse;
import com.climateatlas.app.dto.WeatherReadingResponse;
import com.climateatlas.negocio.model.City;
import com.climateatlas.negocio.model.WeatherReading;
import com.climateatlas.negocio.service.CityService;
import com.climateatlas.negocio.service.WeatherService;
import org.springframework.stereotype.Service;

import java.util.List;

/** Coordina el caso de uso de ciudades y adapta modelos de negocio a DTO HTTP. */
@Service
public class CityApplicationService {
    private final CityService cityService;
    private final WeatherService weatherService;

    /**
     * Constructor: recibe los servicios de negocio que necesita este caso de uso. La inyección evita que esta clase cree dependencias por su cuenta.
     */
    public CityApplicationService(CityService cityService, WeatherService weatherService) {
        this.cityService = cityService;
        this.weatherService = weatherService;
    }

    /**
     * Obtiene todas las ciudades desde negocio y convierte cada modelo City en el DTO que devolverá la API.
     */
    public List<CityResponse> findAll() {
        return cityService.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Busca una ciudad por su identificador y transforma el resultado de negocio en el contrato de respuesta REST.
     */
    public CityResponse findById(Long id) {
        return toResponse(cityService.findById(id));
    }

    /**
     * Extrae los datos validados del DTO de entrada, solicita a negocio registrar la ciudad y convierte el resultado en una respuesta.
     */
    public CityResponse create(CityRequest request) {
        return toResponse(cityService.create(
                request.name(), request.state(), request.latitude(), request.longitude()));
    }

    /**
     * Solicita a negocio modificar los datos de la ciudad indicada y devuelve la versión actualizada como DTO.
     */
    public CityResponse update(Long id, CityRequest request) {
        return toResponse(cityService.update(
                id, request.name(), request.state(), request.latitude(), request.longitude()));
    }

    /**
     * Cambia únicamente el estado habilitado de la ciudad mediante la regla de negocio correspondiente.
     */
    public CityResponse updateEnabled(Long id, Boolean enabled) {
        return toResponse(cityService.updateEnabled(id, enabled));
    }

    /**
     * Solicita a negocio eliminar la ciudad. No construye una respuesta porque la operación no necesita devolver datos.
     */
    public void delete(Long id) {
        cityService.delete(id);
    }

    /**
     * Construye el DTO de una ciudad. También consulta la última medición meteorológica para incluirla como currentWeather; si no existe medición, ese campo queda en null.
     */
    private CityResponse toResponse(City city) {
        WeatherReading latest = weatherService.latestReading(city.getId()).orElse(null);
        WeatherReadingResponse current = latest == null ? null : WeatherReadingResponse.from(latest);
        return CityResponse.from(city, current);
    }
}
