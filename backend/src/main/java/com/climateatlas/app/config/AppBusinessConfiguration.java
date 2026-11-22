package com.climateatlas.app.config;

import com.climateatlas.negocio.port.CityRepository;
import com.climateatlas.negocio.port.WeatherReadingRepository;
import com.climateatlas.negocio.service.CityService;
import com.climateatlas.negocio.service.WeatherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Conecta los servicios puros de negocio con los puertos implementados por repository. */
@Configuration
public class AppBusinessConfiguration {
    /**
     * Registra CityService como bean y le inyecta el puerto CityRepository. El adaptador concreto que implementa ese puerto lo proporciona la capa repository.
     */
    @Bean
    public CityService cityService(CityRepository cityRepository) {
        return new CityService(cityRepository);
    }

    /**
     * Registra WeatherService y le entrega los puertos para consultar ciudades y mediciones. Esta configuración conecta las capas sin introducir dependencias de infraestructura en negocio.
     */
    @Bean
    public WeatherService weatherService(
            CityRepository cityRepository,
            WeatherReadingRepository weatherReadingRepository) {
        return new WeatherService(cityRepository, weatherReadingRepository);
    }
}
