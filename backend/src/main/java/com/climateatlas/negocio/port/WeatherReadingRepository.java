package com.climateatlas.negocio.port;

import com.climateatlas.negocio.model.WeatherReading;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Puerto de persistencia del historial meteorológico. */
public interface WeatherReadingRepository {
    Optional<WeatherReading> findByCityIdAndMeasuredAt(Long cityId, LocalDateTime measuredAt);
    Optional<WeatherReading> findLatestByCityId(Long cityId);
    List<WeatherReading> findHistoryByCityId(Long cityId, LocalDateTime from, LocalDateTime to);
    void saveAll(List<WeatherReading> readings);
    long count();
}
