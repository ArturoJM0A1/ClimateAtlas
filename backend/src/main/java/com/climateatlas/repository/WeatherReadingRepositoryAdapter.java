package com.climateatlas.repository;

import com.climateatlas.negocio.model.City;
import com.climateatlas.negocio.model.WeatherReading;
import com.climateatlas.negocio.port.WeatherReadingRepository;
import com.climateatlas.repository.jpa.SpringDataCityJpaRepository;
import com.climateatlas.repository.jpa.SpringDataWeatherReadingJpaRepository;
import com.climateatlas.repository.jpa.entity.CityEntity;
import com.climateatlas.repository.jpa.entity.WeatherReadingEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Adaptador que oculta Hibernate, Spring Data y SQLite a la capa negocio. */
@Repository
@Transactional
public class WeatherReadingRepositoryAdapter implements WeatherReadingRepository {
    private final SpringDataWeatherReadingJpaRepository weatherRepository;
    private final SpringDataCityJpaRepository cityRepository;

    /**
     * Constructor: recibe los repositorios JPA de mediciones y ciudades. Necesita ambos porque una medición persistida debe asociarse a una CityEntity.
     */
    public WeatherReadingRepositoryAdapter(
            SpringDataWeatherReadingJpaRepository weatherRepository,
            SpringDataCityJpaRepository cityRepository) {
        this.weatherRepository = weatherRepository;
        this.cityRepository = cityRepository;
    }

    /**
     * Busca una lectura por ciudad e instante medido y traduce la entidad encontrada al modelo WeatherReading.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<WeatherReading> findByCityIdAndMeasuredAt(
            Long cityId, LocalDateTime measuredAt) {
        return weatherRepository.findByCity_IdAndMeasuredAt(cityId, measuredAt)
                .map(this::toModel);
    }

    /**
     * Solicita a Spring Data la medición más reciente de la ciudad y la transforma a modelo de negocio.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<WeatherReading> findLatestByCityId(Long cityId) {
        return weatherRepository.findFirstByCity_IdOrderByMeasuredAtDesc(cityId)
                .map(this::toModel);
    }

    /**
     * Consulta las mediciones dentro del intervalo, ordenadas cronológicamente, y convierte cada entidad al modelo del dominio.
     */
    @Override
    @Transactional(readOnly = true)
    public List<WeatherReading> findHistoryByCityId(
            Long cityId, LocalDateTime from, LocalDateTime to) {
        return weatherRepository
                .findByCity_IdAndMeasuredAtBetweenOrderByMeasuredAtAsc(cityId, from, to)
                .stream().map(this::toModel).toList();
    }

    /**
     * Convierte todos los modelos recibidos a entidades JPA y delega la escritura por lotes a Spring Data.
     */
    @Override
    public void saveAll(List<WeatherReading> readings) {
        List<WeatherReadingEntity> entities = readings.stream()
                .map(this::toEntityForSave).toList();
        weatherRepository.saveAll(entities);
    }

    /**
     * Devuelve la cantidad de registros de mediciones.
     */
    @Override
    @Transactional(readOnly = true)
    public long count() {
        return weatherRepository.count();
    }

    /**
     * Obtiene la entidad de ciudad asociada, localiza una lectura existente para la misma ciudad y measuredAt o crea una nueva. Copia los valores y devuelve la entidad lista para guardar.
     */
    private WeatherReadingEntity toEntityForSave(WeatherReading reading) {
        CityEntity city = cityRepository.findById(reading.getCity().getId())
                .orElseThrow(() -> new IllegalStateException(
                        "No existe la ciudad de la lectura: " + reading.getCity().getId()));
        WeatherReadingEntity entity = weatherRepository
                .findByCity_IdAndMeasuredAt(reading.getCity().getId(), reading.getMeasuredAt())
                .orElseGet(WeatherReadingEntity::new);
        entity.updateFrom(city, reading.getTemperature(), reading.getHumidity(),
                reading.getWindSpeed(), reading.getMeasuredAt(), reading.getQueriedAt());
        return entity;
    }

    /**
     * Reconstruye City y WeatherReading a partir de la entidad JPA. La capa de negocio recibe objetos Java independientes de Hibernate.
     */
    private WeatherReading toModel(WeatherReadingEntity entity) {
        CityEntity source = entity.getCity();
        City city = new City(source.getId(), source.getName(), source.getState(),
                source.getLatitude(), source.getLongitude(), source.getEnabled());
        return new WeatherReading(entity.getId(), city, entity.getTemperature(),
                entity.getHumidity(), entity.getWindSpeed(), entity.getMeasuredAt(),
                entity.getQueriedAt());
    }
}
