package com.climateatlas.repository.jpa;

import com.climateatlas.repository.jpa.entity.WeatherReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Consultas derivadas JPA encapsuladas en la capa repository. */
public interface SpringDataWeatherReadingJpaRepository
        extends JpaRepository<WeatherReadingEntity, Long> {
    /**
     * Busca una medición por el id de la ciudad relacionada y por el instante measuredAt.
     */
    Optional<WeatherReadingEntity> findByCity_IdAndMeasuredAt(
            Long cityId, LocalDateTime measuredAt);
    /**
     * Obtiene como máximo un registro de la ciudad, ordenando measuredAt de más reciente a más antiguo.
     */
    Optional<WeatherReadingEntity> findFirstByCity_IdOrderByMeasuredAtDesc(Long cityId);
    /**
     * Recupera las mediciones de una ciudad dentro del intervalo temporal, ordenadas de la más antigua a la más reciente.
     */
    List<WeatherReadingEntity> findByCity_IdAndMeasuredAtBetweenOrderByMeasuredAtAsc(
            Long cityId, LocalDateTime from, LocalDateTime to);
}
