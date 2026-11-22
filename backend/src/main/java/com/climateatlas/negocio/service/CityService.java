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

    /**
     * Constructor: recibe el puerto CityRepository. Así las reglas pueden probarse con dobles de prueba y no conocen la tecnología de almacenamiento.
     */
    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    /**
     * Devuelve el catálogo ordenado a través del puerto de persistencia; no introduce reglas nuevas en la consulta.
     */
    public List<City> findAll() {
        return cityRepository.findAllOrderByName();
    }

    /**
     * Busca la ciudad y lanza ResourceNotFoundException cuando el identificador no existe. Centraliza el comportamiento de búsqueda obligatoria.
     */
    public City findById(Long id) {
        return cityRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No existe la ciudad con id " + id));
    }

    /**
     * Comprueba que no exista otra ciudad con el mismo nombre y estado, normaliza los textos y crea la ciudad habilitada por defecto.
     */
    public City create(String name, String state, Double latitude, Double longitude) {
        ensureNameIsAvailable(name, state, null);
        return cityRepository.save(new City(normalize(name), normalize(state),
                latitude, longitude, true));
    }

    /**
     * Obtiene la ciudad existente, valida que el nombre y estado no estén repetidos por otra ciudad, actualiza sus datos y guarda el resultado.
     */
    public City update(Long id, String name, String state,
                       Double latitude, Double longitude) {
        City city = findById(id);
        ensureNameIsAvailable(name, state, id);
        city.updateDetails(normalize(name), normalize(state), latitude, longitude);
        return cityRepository.save(city);
    }

    /**
     * Busca la ciudad, cambia su estado de habilitación y persiste la modificación.
     */
    public City updateEnabled(Long id, Boolean enabled) {
        City city = findById(id);
        city.setEnabled(enabled);
        return cityRepository.save(city);
    }

    /**
     * Verifica primero que la ciudad exista y después solicita su eliminación. La comprobación permite responder con un error de negocio claro si el id no existe.
     */
    public void delete(Long id) {
        findById(id);
        cityRepository.deleteById(id);
    }

    /**
     * Normaliza nombre y estado, y pregunta al repositorio si existe un duplicado. En una creación revisa toda la tabla; al editar excluye el id de la propia ciudad.
     */
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

    /**
     * Elimina espacios al principio y al final. Si recibe null lo conserva para no provocar un NullPointerException.
     */
    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
