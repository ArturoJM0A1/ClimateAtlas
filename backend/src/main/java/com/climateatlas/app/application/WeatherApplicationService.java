package com.climateatlas.app.application;

import com.climateatlas.app.batch.WeatherJobService;
import com.climateatlas.app.dto.JobExecutionResponse;
import com.climateatlas.app.dto.StatisticsResponse;
import com.climateatlas.app.dto.WeatherReadingResponse;
import com.climateatlas.negocio.service.WeatherService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/** Coordina las consultas meteorológicas y el inicio del Job desde la capa app. */
@Service
public class WeatherApplicationService {
    private final WeatherService weatherService;
    private final WeatherJobService weatherJobService;

    /**
     * Constructor: recibe el servicio de consultas meteorológicas y el componente que inicia Spring Batch.
     */
    public WeatherApplicationService(
            WeatherService weatherService, WeatherJobService weatherJobService) {
        this.weatherService = weatherService;
        this.weatherJobService = weatherJobService;
    }

    /**
     * Consulta el historial para una ciudad y un intervalo de tiempo. Convierte cada medición del dominio al DTO de respuesta HTTP.
     */
    public List<WeatherReadingResponse> history(
            Long cityId, LocalDateTime from, LocalDateTime to) {
        return weatherService.history(cityId, from, to).stream()
                .map(WeatherReadingResponse::from).toList();
    }

    /**
     * Obtiene las estadísticas calculadas por negocio y las adapta al formato de salida de la API.
     */
    public StatisticsResponse statistics() {
        return StatisticsResponse.from(weatherService.statistics());
    }

    /**
     * Delega el inicio del proceso de actualización a WeatherJobService y devuelve la información de la ejecución Batch.
     */
    public JobExecutionResponse refresh() {
        return weatherJobService.start();
    }
}
