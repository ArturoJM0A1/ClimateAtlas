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

    /**
     * Constructor: recibe los puertos de ciudades y mediciones. Con ellos puede verificar la existencia de ciudades y consultar datos sin depender de persistencia concreta.
     */
    public WeatherService(CityRepository cityRepository, WeatherReadingRepository readingRepository) {
        this.cityRepository = cityRepository;
        this.readingRepository = readingRepository;
    }

    /**
     * Comprueba que la ciudad exista y después devuelve su medición más reciente, si hay alguna.
     */
    public Optional<WeatherReading> latestReading(Long cityId) {
        if (!cityRepository.existsById(cityId)) {
            throw new ResourceNotFoundException("No existe la ciudad con id " + cityId);
        }
        return readingRepository.findLatestByCityId(cityId);
    }

    /**
     * Valida que exista la ciudad y que from no sea posterior a to. Si el intervalo es correcto, delega la consulta del historial al repositorio.
     */
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

    /**
     * Consulta las ciudades, obtiene la última lectura de cada una y calcula el promedio de sus temperaturas. También devuelve el total de ciudades, las habilitadas y las lecturas almacenadas; el promedio es null si no hay lecturas.
     */
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
