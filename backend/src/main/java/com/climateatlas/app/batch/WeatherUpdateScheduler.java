package com.climateatlas.app.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Scheduling decide cuándo inicia el Job; Spring Batch realiza el procesamiento. */
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
            LOGGER.info("Resultado del inicio Batch: {}", weatherJobService.start());
        } catch (RuntimeException exception) {
            LOGGER.error("Falló el inicio de la actualización automática.", exception);
        }
    }
}
