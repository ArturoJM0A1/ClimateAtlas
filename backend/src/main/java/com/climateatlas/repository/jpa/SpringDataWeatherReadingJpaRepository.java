package com.climateatlas.repository.jpa;

import com.climateatlas.repository.jpa.entity.WeatherReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Consultas derivadas JPA encapsuladas en la capa repository. */
public interface SpringDataWeatherReadingJpaRepository
        extends JpaRepository<WeatherReadingEntity, Long> {
    Optional<WeatherReadingEntity> findByCity_IdAndMeasuredAt(
            Long cityId, LocalDateTime measuredAt);
    Optional<WeatherReadingEntity> findFirstByCity_IdOrderByMeasuredAtDesc(Long cityId);
    List<WeatherReadingEntity> findByCity_IdAndMeasuredAtBetweenOrderByMeasuredAtAsc(
            Long cityId, LocalDateTime from, LocalDateTime to);
}
