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
    @Bean
    public CityService cityService(CityRepository cityRepository) {
        return new CityService(cityRepository);
    }

    @Bean
    public WeatherService weatherService(
            CityRepository cityRepository,
            WeatherReadingRepository weatherReadingRepository) {
        return new WeatherService(cityRepository, weatherReadingRepository);
    }
}
