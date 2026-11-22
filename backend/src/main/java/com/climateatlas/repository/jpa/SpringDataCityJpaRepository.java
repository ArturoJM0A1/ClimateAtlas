package com.climateatlas.repository.jpa;

import com.climateatlas.repository.jpa.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Interfaz Spring Data interna; negocio nunca la importa. */
public interface SpringDataCityJpaRepository extends JpaRepository<CityEntity, Long> {
    List<CityEntity> findAllByOrderByNameAsc();
    List<CityEntity> findByEnabledTrueOrderByIdAsc();
    boolean existsByNameIgnoreCaseAndStateIgnoreCase(String name, String state);
    boolean existsByNameIgnoreCaseAndStateIgnoreCaseAndIdNot(
            String name, String state, Long id);
    long countByEnabledTrue();
}
