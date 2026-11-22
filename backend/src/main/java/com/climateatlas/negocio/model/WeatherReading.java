package com.climateatlas.negocio.model;

import java.time.LocalDateTime;

/** Modelo de negocio de una medición meteorológica. */
public class WeatherReading {
    private final Long id;
    private final City city;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private final LocalDateTime measuredAt;
    private LocalDateTime queriedAt;

    public WeatherReading(Long id, City city, Double temperature, Double humidity,
                          Double windSpeed, LocalDateTime measuredAt, LocalDateTime queriedAt) {
        this.id = id;
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.measuredAt = measuredAt;
        this.queriedAt = queriedAt;
    }

    public void updateMeasurement(Double temperature, Double humidity,
                                  Double windSpeed, LocalDateTime queriedAt) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.queriedAt = queriedAt;
    }

    public Long getId() { return id; }
    public City getCity() { return city; }
    public Double getTemperature() { return temperature; }
    public Double getHumidity() { return humidity; }
    public Double getWindSpeed() { return windSpeed; }
    public LocalDateTime getMeasuredAt() { return measuredAt; }
    public LocalDateTime getQueriedAt() { return queriedAt; }
}
