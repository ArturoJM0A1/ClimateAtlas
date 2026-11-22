package com.climateatlas.app.config;

import com.climateatlas.negocio.service.CityService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Carga las ciudades de muestra usando el servicio de negocio, sin conocer JPA. */
@Configuration
public class InitialCitiesConfiguration {
    @Bean
    public CommandLineRunner seedInitialCities(CityService cityService) {
        return args -> {
            if (!cityService.findAll().isEmpty()) {
                return;
            }
            cityService.create("Monterrey", "Nuevo León", 25.6866, -100.3161);
            cityService.create("Guadalajara", "Jalisco", 20.6597, -103.3496);
            cityService.create("Ciudad de México", "Ciudad de México", 19.4326, -99.1332);
            cityService.create("Pachuca", "Hidalgo", 20.1011, -98.7591);
            cityService.create("Mérida", "Yucatán", 20.9674, -89.5926);
            cityService.create("Querétaro", "Querétaro", 20.5888, -100.3899);
            cityService.create("Tijuana", "Baja California", 32.5149, -117.0382);
        };
    }
}
