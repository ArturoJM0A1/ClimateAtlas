package com.climateatlas.negocio.port;

import com.climateatlas.negocio.model.City;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia del catálogo definido por negocio.
 * La capa negocio depende del contrato, no de Spring Data ni SQLite.
 */
public interface CityRepository {
    List<City> findAllOrderByName();
    List<City> findEnabled();
    Optional<City> findById(Long id);
    City save(City city);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByNameAndState(String name, String state);
    boolean existsByNameAndStateAndIdNot(String name, String state, Long id);
    long count();
    long countEnabled();
}
