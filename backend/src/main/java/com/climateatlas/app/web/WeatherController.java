package com.climateatlas.app.web;

import com.climateatlas.app.application.WeatherApplicationService;
import com.climateatlas.app.dto.JobExecutionResponse;
import com.climateatlas.app.dto.StatisticsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Endpoints HTTP de estadísticas y actualización meteorológica. */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherApplicationService weatherApplicationService;

    /**
     * Constructor: recibe el servicio de aplicación que coordina las consultas y el inicio de Spring Batch.
     */
    public WeatherController(WeatherApplicationService weatherApplicationService) {
        this.weatherApplicationService = weatherApplicationService;
    }

    /**
     * Atiende GET /api/weather/statistics y devuelve los contadores y el promedio de temperatura actual.
     */
    @GetMapping("/statistics")
    public StatisticsResponse statistics() {
        return weatherApplicationService.statistics();
    }

    /**
     * Atiende POST /api/weather/refresh. Solicita iniciar el Job y responde HTTP 202 Accepted porque el procesamiento se ejecuta como trabajo Batch y puede continuar después de la respuesta.
     */
    @PostMapping("/refresh")
    public ResponseEntity<JobExecutionResponse> refresh() {
        return ResponseEntity.accepted().body(weatherApplicationService.refresh());
    }
}
