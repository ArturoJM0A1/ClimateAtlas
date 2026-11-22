package com.climateatlas.negocio.port;

import com.climateatlas.negocio.model.City;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia del catálogo definido por negocio.
 * La capa negocio depende del contrato, no de Spring Data ni SQLite.
 */
public interface CityRepository {
    /**
     * Devuelve todas las ciudades ordenadas por nombre. El contrato no depende de una consulta SQL ni de una tecnología concreta.
     */
    List<City> findAllOrderByName();
    /**
     * Devuelve las ciudades habilitadas para ser procesadas por Spring Batch.
     */
    List<City> findEnabled();
    /**
     * Busca una ciudad por su identificador; Optional vacío representa que no existe.
     */
    Optional<City> findById(Long id);
    /**
     * Persiste una ciudad nueva o actualizada y devuelve el modelo con el identificador resultante.
     */
    City save(City city);
    /**
     * Elimina la ciudad indicada por su identificador.
     */
    void deleteById(Long id);
    /**
     * Comprueba si existe una ciudad con el id recibido.
     */
    boolean existsById(Long id);
    /**
     * Comprueba si el par nombre y estado ya está registrado.
     */
    boolean existsByNameAndState(String name, String state);
    /**
     * Comprueba si el par nombre y estado pertenece a otra ciudad distinta del id indicado.
     */
    boolean existsByNameAndStateAndIdNot(String name, String state, Long id);
    /**
     * Devuelve el número total de ciudades registradas.
     */
    long count();
    /**
     * Devuelve el número de ciudades habilitadas.
     */
    long countEnabled();
}
