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

    public WeatherController(WeatherApplicationService weatherApplicationService) {
        this.weatherApplicationService = weatherApplicationService;
    }

    @GetMapping("/statistics")
    public StatisticsResponse statistics() {
        return weatherApplicationService.statistics();
    }

    @PostMapping("/refresh")
    public ResponseEntity<JobExecutionResponse> refresh() {
        return ResponseEntity.accepted().body(weatherApplicationService.refresh());
    }
}
