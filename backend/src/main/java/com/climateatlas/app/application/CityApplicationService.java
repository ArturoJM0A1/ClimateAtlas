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

    public CityApplicationService(CityService cityService, WeatherService weatherService) {
        this.cityService = cityService;
        this.weatherService = weatherService;
    }

    public List<CityResponse> findAll() {
        return cityService.findAll().stream().map(this::toResponse).toList();
    }

    public CityResponse findById(Long id) {
        return toResponse(cityService.findById(id));
    }

    public CityResponse create(CityRequest request) {
        return toResponse(cityService.create(
                request.name(), request.state(), request.latitude(), request.longitude()));
    }

    public CityResponse update(Long id, CityRequest request) {
        return toResponse(cityService.update(
                id, request.name(), request.state(), request.latitude(), request.longitude()));
    }

    public CityResponse updateEnabled(Long id, Boolean enabled) {
        return toResponse(cityService.updateEnabled(id, enabled));
    }

    public void delete(Long id) {
        cityService.delete(id);
    }

    private CityResponse toResponse(City city) {
        WeatherReading latest = weatherService.latestReading(city.getId()).orElse(null);
        WeatherReadingResponse current = latest == null ? null : WeatherReadingResponse.from(latest);
        return CityResponse.from(city, current);
    }
}
