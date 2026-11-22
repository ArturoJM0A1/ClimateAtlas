package com.climateatlas.repository.jpa;

import com.climateatlas.repository.jpa.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Interfaz Spring Data interna; negocio nunca la importa. */
public interface SpringDataCityJpaRepository extends JpaRepository<CityEntity, Long> {
    /**
     * Método derivado de Spring Data: recupera todas las ciudades ordenadas ascendentemente por el campo name.
     */
    List<CityEntity> findAllByOrderByNameAsc();
    /**
     * Método derivado: selecciona ciudades cuyo campo enabled es true y ordena el resultado por id ascendente.
     */
    List<CityEntity> findByEnabledTrueOrderByIdAsc();
    /**
     * Devuelve true si existe una ciudad cuyo nombre y estado coinciden sin distinguir mayúsculas y minúsculas.
     */
    boolean existsByNameIgnoreCaseAndStateIgnoreCase(String name, String state);
    /**
     * Busca una coincidencia de nombre y estado ignorando mayúsculas, pero excluye el id indicado.
     */
    boolean existsByNameIgnoreCaseAndStateIgnoreCaseAndIdNot(
            String name, String state, Long id);
    /**
     * Cuenta las ciudades cuyo indicador enabled es true.
     */
    long countByEnabledTrue();
}
