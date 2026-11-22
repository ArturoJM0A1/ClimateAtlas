package com.climateatlas.repository;

import com.climateatlas.negocio.model.City;
import com.climateatlas.negocio.port.CityRepository;
import com.climateatlas.repository.jpa.SpringDataCityJpaRepository;
import com.climateatlas.repository.jpa.entity.CityEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto CityRepository con Spring Data JPA.
 * Aquí se traducen modelos de negocio a entidades persistentes.
 */
@Repository
@Transactional
public class CityRepositoryAdapter implements CityRepository {
    private final SpringDataCityJpaRepository jpaRepository;

    /**
     * Constructor: recibe el repositorio Spring Data que trabaja con CityEntity. Este adaptador es el punto donde el puerto de negocio se conecta con JPA.
     */
    public CityRepositoryAdapter(SpringDataCityJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Obtiene entidades ordenadas por nombre y transforma cada una al modelo City que entiende negocio. La transacción es de solo lectura.
     */
    @Override
    @Transactional(readOnly = true)
    public List<City> findAllOrderByName() {
        return jpaRepository.findAllByOrderByNameAsc().stream().map(this::toModel).toList();
    }

    /**
     * Consulta únicamente las entidades habilitadas y convierte los resultados a modelos de negocio.
     */
    @Override
    @Transactional(readOnly = true)
    public List<City> findEnabled() {
        return jpaRepository.findByEnabledTrueOrderByIdAsc().stream().map(this::toModel).toList();
    }

    /**
     * Busca la entidad por id y transforma el Optional para que negocio reciba City, no CityEntity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<City> findById(Long id) {
        return jpaRepository.findById(id).map(this::toModel);
    }

    /**
     * Convierte el modelo City a una entidad. Si no tiene id crea una entidad nueva; si tiene id recupera la existente. Copia los atributos, guarda mediante JPA y devuelve el modelo resultante.
     */
    @Override
    public City save(City city) {
        CityEntity entity = city.getId() == null
                ? new CityEntity()
                : jpaRepository.findById(city.getId()).orElseGet(CityEntity::new);
        entity.updateFrom(city.getName(), city.getState(), city.getLatitude(),
                city.getLongitude(), city.getEnabled());
        return toModel(jpaRepository.save(entity));
    }

    /**
     * Delega a Spring Data la eliminación de la ciudad indicada.
     */
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    /**
     * Comprueba la existencia del id en la base de datos.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    /**
     * Consulta si existe una ciudad con el mismo nombre y estado, ignorando diferencias entre mayúsculas y minúsculas.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndState(String name, String state) {
        return jpaRepository.existsByNameIgnoreCaseAndStateIgnoreCase(name, state);
    }

    /**
     * Consulta si existe una ciudad duplicada con nombre y estado iguales, excluyendo el id proporcionado para permitir la edición del registro actual.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndStateAndIdNot(String name, String state, Long id) {
        return jpaRepository.existsByNameIgnoreCaseAndStateIgnoreCaseAndIdNot(
                name, state, id);
    }

    /**
     * Devuelve el número de filas de la tabla de ciudades.
     */
    @Override
    @Transactional(readOnly = true)
    public long count() {
        return jpaRepository.count();
    }

    /**
     * Devuelve cuántas filas tienen enabled = true.
     */
    @Override
    @Transactional(readOnly = true)
    public long countEnabled() {
        return jpaRepository.countByEnabledTrue();
    }

    /**
     * Transforma una entidad JPA en un objeto City puro. Evita que las anotaciones o tipos de persistencia salgan de la capa repository.
     */
    private City toModel(CityEntity entity) {
        return new City(entity.getId(), entity.getName(), entity.getState(),
                entity.getLatitude(), entity.getLongitude(), entity.getEnabled());
    }
}
