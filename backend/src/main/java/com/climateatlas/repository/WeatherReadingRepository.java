package com.climateatlas.repository;

import com.climateatlas.domain.WeatherReading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Consultas de historial y de la última lectura de cada ciudad. */
public interface WeatherReadingRepository extends JpaRepository<WeatherReading, Long> {
    Optional<WeatherReading> findByCity_IdAndMeasuredAt(Long cityId, LocalDateTime measuredAt);
    Optional<WeatherReading> findFirstByCity_IdOrderByMeasuredAtDesc(Long cityId);
    List<WeatherReading> findByCity_IdAndMeasuredAtBetweenOrderByMeasuredAtAsc(
            Long cityId, LocalDateTime from, LocalDateTime to);
}
