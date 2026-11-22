package com.climateatlas.service;

import com.climateatlas.domain.City;
import com.climateatlas.domain.WeatherReading;
import com.climateatlas.dto.StatisticsResponse;
import com.climateatlas.dto.WeatherReadingResponse;
import com.climateatlas.exception.ResourceNotFoundException;
import com.climateatlas.repository.CityRepository;
import com.climateatlas.repository.WeatherReadingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.List;

/** Consultas de historial y estadísticas sobre mediciones persistidas. */
@Service
@Transactional(readOnly = true)
public class WeatherService {

    private final CityRepository cityRepository;
    private final WeatherReadingRepository weatherReadingRepository;

    public WeatherService(CityRepository cityRepository,
                          WeatherReadingRepository weatherReadingRepository) {
        this.cityRepository = cityRepository;
        this.weatherReadingRepository = weatherReadingRepository;
    }

    public List<WeatherReadingResponse> history(Long cityId, LocalDateTime from, LocalDateTime to) {
        if (!cityRepository.existsById(cityId)) {
            throw new ResourceNotFoundException("No existe la ciudad con id " + cityId);
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser posterior a la fecha final.");
        }
        return weatherReadingRepository
                .findByCity_IdAndMeasuredAtBetweenOrderByMeasuredAtAsc(cityId, from, to)
                .stream().map(WeatherReadingResponse::from).toList();
    }

    public StatisticsResponse statistics() {
        List<City> cities = cityRepository.findAll();
        OptionalDouble average = cities.stream()
                .map(city -> weatherReadingRepository
                        .findFirstByCity_IdOrderByMeasuredAtDesc(city.getId()))
                .flatMap(Optional::stream)
                .mapToDouble(WeatherReading::getTemperature)
                .average();

        return new StatisticsResponse(cityRepository.count(), cityRepository.countByEnabledTrue(),
                weatherReadingRepository.count(), average.isPresent() ? average.getAsDouble() : null);
    }
}
