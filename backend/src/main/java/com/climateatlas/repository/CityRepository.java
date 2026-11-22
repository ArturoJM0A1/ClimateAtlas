package com.climateatlas.repository;

import com.climateatlas.domain.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** CRUD y consultas derivadas sobre la entidad City. */
public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findAllByOrderByNameAsc();
    boolean existsByNameIgnoreCaseAndStateIgnoreCase(String name, String state);
    boolean existsByNameIgnoreCaseAndStateIgnoreCaseAndIdNot(String name, String state, Long id);
    long countByEnabledTrue();
    Page<City> findByEnabledTrue(Pageable pageable);
}
