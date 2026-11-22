package com.climateatlas.negocio.service;

import com.climateatlas.negocio.exception.ResourceNotFoundException;
import com.climateatlas.negocio.model.City;
import com.climateatlas.negocio.model.WeatherReading;
import com.climateatlas.negocio.model.WeatherStatistics;
import com.climateatlas.negocio.port.CityRepository;
import com.climateatlas.negocio.port.WeatherReadingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/** Reglas de consulta de historial y cálculo de estadísticas del dominio. */
public class WeatherService {
    private final CityRepository cityRepository;
    private final WeatherReadingRepository readingRepository;

    public WeatherService(CityRepository cityRepository, WeatherReadingRepository readingRepository) {
        this.cityRepository = cityRepository;
        this.readingRepository = readingRepository;
    }

    public Optional<WeatherReading> latestReading(Long cityId) {
        if (!cityRepository.existsById(cityId)) {
            throw new ResourceNotFoundException("No existe la ciudad con id " + cityId);
        }
        return readingRepository.findLatestByCityId(cityId);
    }

    public List<WeatherReading> history(Long cityId, LocalDateTime from, LocalDateTime to) {
        if (!cityRepository.existsById(cityId)) {
            throw new ResourceNotFoundException("No existe la ciudad con id " + cityId);
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser posterior a la fecha final.");
        }
        return readingRepository.findHistoryByCityId(cityId, from, to);
    }

    public WeatherStatistics statistics() {
        List<City> cities = cityRepository.findAllOrderByName();
        OptionalDouble average = cities.stream()
                .map(city -> readingRepository.findLatestByCityId(city.getId()))
                .flatMap(Optional::stream)
                .mapToDouble(WeatherReading::getTemperature)
                .average();

        return new WeatherStatistics(cityRepository.count(), cityRepository.countEnabled(),
                readingRepository.count(), average.isPresent() ? average.getAsDouble() : null);
    }
}
