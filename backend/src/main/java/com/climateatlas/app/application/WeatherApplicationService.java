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

    public WeatherApplicationService(
            WeatherService weatherService, WeatherJobService weatherJobService) {
        this.weatherService = weatherService;
        this.weatherJobService = weatherJobService;
    }

    public List<WeatherReadingResponse> history(
            Long cityId, LocalDateTime from, LocalDateTime to) {
        return weatherService.history(cityId, from, to).stream()
                .map(WeatherReadingResponse::from).toList();
    }

    public StatisticsResponse statistics() {
        return StatisticsResponse.from(weatherService.statistics());
    }

    public JobExecutionResponse refresh() {
        return weatherJobService.start();
    }
}
