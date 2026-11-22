package com.climateatlas.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Medición meteorológica tomada para una ciudad.
 * city_id + measured_at es único para impedir duplicados. queried_at guarda
 * cuándo consultó el backend al proveedor, no el instante de la observación.
 */
@Entity
@Table(name = "weather_readings",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_weather_city_measured_at",
                columnNames = {"city_id", "measured_at"}),
        indexes = @Index(
                name = "idx_weather_city_measured_at",
                columnList = "city_id, measured_at"))
public class WeatherReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_weather_city"))
    private City city;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double humidity;

    @Column(name = "wind_speed", nullable = false)
    private Double windSpeed;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @Column(name = "queried_at", nullable = false)
    private LocalDateTime queriedAt;

    protected WeatherReading() {
        // Constructor requerido por JPA.
    }

    public WeatherReading(City city, Double temperature, Double humidity, Double windSpeed,
                          LocalDateTime measuredAt, LocalDateTime queriedAt) {
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.measuredAt = measuredAt;
        this.queriedAt = queriedAt;
    }

    public void updateMeasurement(Double temperature, Double humidity, Double windSpeed,
                                  LocalDateTime queriedAt) {
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
