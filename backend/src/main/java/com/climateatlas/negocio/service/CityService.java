package com.climateatlas.negocio.service;

import com.climateatlas.negocio.exception.ResourceNotFoundException;
import com.climateatlas.negocio.model.City;
import com.climateatlas.negocio.port.CityRepository;
import java.util.List;

/**
 * Reglas de negocio del catálogo. Es Java puro y utiliza únicamente un puerto
 * para persistir; no importa Spring, DTO ni entidades JPA.
 */
public class CityService {
    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<City> findAll() {
        return cityRepository.findAllOrderByName();
    }

    public City findById(Long id) {
        return cityRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No existe la ciudad con id " + id));
    }

    public City create(String name, String state, Double latitude, Double longitude) {
        ensureNameIsAvailable(name, state, null);
        return cityRepository.save(new City(normalize(name), normalize(state),
                latitude, longitude, true));
    }

    public City update(Long id, String name, String state,
                       Double latitude, Double longitude) {
        City city = findById(id);
        ensureNameIsAvailable(name, state, id);
        city.updateDetails(normalize(name), normalize(state), latitude, longitude);
        return cityRepository.save(city);
    }

    public City updateEnabled(Long id, Boolean enabled) {
        City city = findById(id);
        city.setEnabled(enabled);
        return cityRepository.save(city);
    }

    public void delete(Long id) {
        findById(id);
        cityRepository.deleteById(id);
    }

    private void ensureNameIsAvailable(String name, String state, Long currentId) {
        String normalizedName = normalize(name);
        String normalizedState = normalize(state);
        boolean duplicate = currentId == null
                ? cityRepository.existsByNameAndState(normalizedName, normalizedState)
                : cityRepository.existsByNameAndStateAndIdNot(
                        normalizedName, normalizedState, currentId);
        if (duplicate) {
            throw new IllegalArgumentException(
                    "Ya existe una ciudad con ese nombre y estado.");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
