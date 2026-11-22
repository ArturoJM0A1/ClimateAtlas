package com.climateatlas.service;

import com.climateatlas.domain.City;
import com.climateatlas.domain.WeatherReading;
import com.climateatlas.dto.CityRequest;
import com.climateatlas.dto.CityResponse;
import com.climateatlas.dto.WeatherReadingResponse;
import com.climateatlas.exception.ResourceNotFoundException;
import com.climateatlas.repository.CityRepository;
import com.climateatlas.repository.WeatherReadingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio del catálogo de ciudades.
 * El controlador maneja HTTP; este servicio coordina persistencia y validaciones.
 */
@Service
@Transactional
public class CityService {

    private final CityRepository cityRepository;
    private final WeatherReadingRepository weatherReadingRepository;

    public CityService(CityRepository cityRepository, WeatherReadingRepository weatherReadingRepository) {
        this.cityRepository = cityRepository;
        this.weatherReadingRepository = weatherReadingRepository;
    }

    @Transactional(readOnly = true)
    public List<CityResponse> findAll() {
        return cityRepository.findAllByOrderByNameAsc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CityResponse findById(Long id) {
        return toResponse(findCity(id));
    }

    public CityResponse create(CityRequest request) {
        ensureNameIsAvailable(request.name(), request.state(), null);
        City city = new City(request.name().trim(), request.state().trim(),
                request.latitude(), request.longitude(), true);
        return toResponse(cityRepository.save(city));
    }

    public CityResponse update(Long id, CityRequest request) {
        City city = findCity(id);
        ensureNameIsAvailable(request.name(), request.state(), id);
        city.updateDetails(request.name().trim(), request.state().trim(),
                request.latitude(), request.longitude());
        return toResponse(cityRepository.save(city));
    }

    public CityResponse updateEnabled(Long id, Boolean enabled) {
        City city = findCity(id);
        city.setEnabled(enabled);
        return toResponse(cityRepository.save(city));
    }

    public void delete(Long id) {
        // City define cascade REMOVE para no dejar lecturas huérfanas.
        cityRepository.delete(findCity(id));
    }

    private City findCity(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la ciudad con id " + id));
    }

    private void ensureNameIsAvailable(String name, String state, Long currentId) {
        boolean exists = currentId == null
                ? cityRepository.existsByNameIgnoreCaseAndStateIgnoreCase(name.trim(), state.trim())
                : cityRepository.existsByNameIgnoreCaseAndStateIgnoreCaseAndIdNot(
                        name.trim(), state.trim(), currentId);
        if (exists) {
            throw new IllegalArgumentException("Ya existe una ciudad con ese nombre y estado.");
        }
    }

    private CityResponse toResponse(City city) {
        WeatherReading latest = weatherReadingRepository
                .findFirstByCity_IdOrderByMeasuredAtDesc(city.getId()).orElse(null);
        WeatherReadingResponse current = latest == null ? null : WeatherReadingResponse.from(latest);
        return CityResponse.from(city, current);
    }
}
