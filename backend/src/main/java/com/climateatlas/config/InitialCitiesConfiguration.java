package com.climateatlas.config;

import com.climateatlas.domain.City;
import com.climateatlas.repository.CityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

/**
 * Inserta ciudades solo cuando la tabla está vacía.
 * Las coordenadas son latitud y longitud en grados decimales (WGS 84).
 */
@Configuration
public class InitialCitiesConfiguration {

    @Bean
    public CommandLineRunner seedInitialCities(CityRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.saveAll(List.of(
                    new City("Monterrey", "Nuevo León", 25.6866, -100.3161, true),
                    new City("Guadalajara", "Jalisco", 20.6597, -103.3496, true),
                    new City("Ciudad de México", "Ciudad de México", 19.4326, -99.1332, true),
                    new City("Pachuca", "Hidalgo", 20.1011, -98.7591, true),
                    new City("Mérida", "Yucatán", 20.9674, -89.5926, true),
                    new City("Querétaro", "Querétaro", 20.5888, -100.3899, true),
                    new City("Tijuana", "Baja California", 32.5149, -117.0382, true)
            ));
        };
    }
}
