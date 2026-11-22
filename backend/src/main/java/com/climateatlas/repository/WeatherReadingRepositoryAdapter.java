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

    public WeatherReadingRepositoryAdapter(
            SpringDataWeatherReadingJpaRepository weatherRepository,
            SpringDataCityJpaRepository cityRepository) {
        this.weatherRepository = weatherRepository;
        this.cityRepository = cityRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WeatherReading> findByCityIdAndMeasuredAt(
            Long cityId, LocalDateTime measuredAt) {
        return weatherRepository.findByCity_IdAndMeasuredAt(cityId, measuredAt)
                .map(this::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WeatherReading> findLatestByCityId(Long cityId) {
        return weatherRepository.findFirstByCity_IdOrderByMeasuredAtDesc(cityId)
                .map(this::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WeatherReading> findHistoryByCityId(
            Long cityId, LocalDateTime from, LocalDateTime to) {
        return weatherRepository
                .findByCity_IdAndMeasuredAtBetweenOrderByMeasuredAtAsc(cityId, from, to)
                .stream().map(this::toModel).toList();
    }

    @Override
    public void saveAll(List<WeatherReading> readings) {
        List<WeatherReadingEntity> entities = readings.stream()
                .map(this::toEntityForSave).toList();
        weatherRepository.saveAll(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return weatherRepository.count();
    }

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

    private WeatherReading toModel(WeatherReadingEntity entity) {
        CityEntity source = entity.getCity();
        City city = new City(source.getId(), source.getName(), source.getState(),
                source.getLatitude(), source.getLongitude(), source.getEnabled());
        return new WeatherReading(entity.getId(), city, entity.getTemperature(),
                entity.getHumidity(), entity.getWindSpeed(), entity.getMeasuredAt(),
                entity.getQueriedAt());
    }
}
