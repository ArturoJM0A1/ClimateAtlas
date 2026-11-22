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

    /**
     * Constructor: recibe el servicio que inicia el Job. El scheduler solo decide cuándo dispararlo; no ejecuta el procesamiento por sí mismo.
     */
    public WeatherUpdateScheduler(WeatherJobService weatherJobService) {
        this.weatherJobService = weatherJobService;
    }

    @Scheduled(
            fixedDelayString = "${climate.weather.fixed-delay-ms:3600000}",
            initialDelayString = "${climate.weather.initial-delay-ms:15000}")
    /**
     * Método invocado por Spring según los intervalos configurados. Inicia una ejecución Batch y registra el resultado; captura errores para que una falla de inicio no detenga el planificador.
     */
    public void updateWeatherAutomatically() {
        try {
            LOGGER.info("Comenzando actualización meteorológica programada.");
            LOGGER.info("Resultado del inicio Batch: {}", weatherJobService.start());
        } catch (RuntimeException exception) {
            LOGGER.error("Falló el inicio de la actualización automática.", exception);
        }
    }
}
