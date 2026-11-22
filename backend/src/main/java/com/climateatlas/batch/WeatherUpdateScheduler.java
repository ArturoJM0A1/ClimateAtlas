package com.climateatlas.batch;

import com.climateatlas.service.WeatherJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduling decide cuándo comienza el Job. No sustituye a Spring Batch:
 * el procesamiento de cada ciudad sigue ocurriendo dentro del Step.
 */
@Component
public class WeatherUpdateScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherUpdateScheduler.class);
    private final WeatherJobService weatherJobService;

    public WeatherUpdateScheduler(WeatherJobService weatherJobService) {
        this.weatherJobService = weatherJobService;
    }

    @Scheduled(
            fixedDelayString = "${climate.weather.fixed-delay-ms:3600000}",
            initialDelayString = "${climate.weather.initial-delay-ms:15000}")
    public void updateWeatherAutomatically() {
        try {
            LOGGER.info("Comenzando actualización meteorológica programada.");
            LOGGER.info("Resultado de inicio Batch: {}", weatherJobService.start());
        } catch (RuntimeException exception) {
            // Una excepción no cancela las siguientes ejecuciones del scheduler.
            LOGGER.error("Falló el inicio de la actualización automática.", exception);
        }
    }
}
