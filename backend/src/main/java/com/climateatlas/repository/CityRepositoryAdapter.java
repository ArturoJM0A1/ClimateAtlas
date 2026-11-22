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

    public CityRepositoryAdapter(SpringDataCityJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<City> findAllOrderByName() {
        return jpaRepository.findAllByOrderByNameAsc().stream().map(this::toModel).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<City> findEnabled() {
        return jpaRepository.findByEnabledTrueOrderByIdAsc().stream().map(this::toModel).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<City> findById(Long id) {
        return jpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public City save(City city) {
        CityEntity entity = city.getId() == null
                ? new CityEntity()
                : jpaRepository.findById(city.getId()).orElseGet(CityEntity::new);
        entity.updateFrom(city.getName(), city.getState(), city.getLatitude(),
                city.getLongitude(), city.getEnabled());
        return toModel(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndState(String name, String state) {
        return jpaRepository.existsByNameIgnoreCaseAndStateIgnoreCase(name, state);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndStateAndIdNot(String name, String state, Long id) {
        return jpaRepository.existsByNameIgnoreCaseAndStateIgnoreCaseAndIdNot(
                name, state, id);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return jpaRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countEnabled() {
        return jpaRepository.countByEnabledTrue();
    }

    private City toModel(CityEntity entity) {
        return new City(entity.getId(), entity.getName(), entity.getState(),
                entity.getLatitude(), entity.getLongitude(), entity.getEnabled());
    }
}
