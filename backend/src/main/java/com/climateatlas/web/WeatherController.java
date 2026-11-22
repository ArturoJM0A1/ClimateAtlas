package com.climateatlas.web;

import com.climateatlas.dto.JobExecutionResponse;
import com.climateatlas.dto.StatisticsResponse;
import com.climateatlas.service.WeatherJobService;
import com.climateatlas.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Estadísticas y disparo manual del Job de Spring Batch. */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherJobService weatherJobService;

    public WeatherController(WeatherService weatherService, WeatherJobService weatherJobService) {
        this.weatherService = weatherService;
        this.weatherJobService = weatherJobService;
    }

    @GetMapping("/statistics")
    public StatisticsResponse statistics() {
        return weatherService.statistics();
    }

    @PostMapping("/refresh")
    public ResponseEntity<JobExecutionResponse> refresh() {
        return ResponseEntity.accepted().body(weatherJobService.start());
    }
}
