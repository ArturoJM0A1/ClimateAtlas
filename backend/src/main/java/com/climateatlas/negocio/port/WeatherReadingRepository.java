package com.climateatlas.negocio.port;

import com.climateatlas.negocio.model.WeatherReading;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Puerto de persistencia del historial meteorológico. */
public interface WeatherReadingRepository {
    /**
     * Busca una lectura por la combinación de ciudad e instante medido. Este criterio permite evitar insertar dos veces el mismo dato temporal.
     */
    Optional<WeatherReading> findByCityIdAndMeasuredAt(Long cityId, LocalDateTime measuredAt);
    /**
     * Devuelve la última lectura de una ciudad, ordenada por el momento de medición.
     */
    Optional<WeatherReading> findLatestByCityId(Long cityId);
    /**
     * Devuelve las lecturas de una ciudad dentro del intervalo indicado, en orden cronológico ascendente.
     */
    List<WeatherReading> findHistoryByCityId(Long cityId, LocalDateTime from, LocalDateTime to);
    /**
     * Guarda en conjunto las mediciones recibidas. La implementación decide el mecanismo de persistencia.
     */
    void saveAll(List<WeatherReading> readings);
    /**
     * Devuelve la cantidad total de mediciones almacenadas.
     */
    long count();
}
