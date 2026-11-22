package com.climateatlas.repository.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Entidad JPA de la tabla weather_readings. */
@Entity
@Table(name = "weather_readings",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_weather_city_measured_at",
                columnNames = {"city_id", "measured_at"}),
        indexes = @Index(
                name = "idx_weather_city_measured_at",
                columnList = "city_id, measured_at"))
public class WeatherReadingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_weather_city"))
    private CityEntity city;

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

    /** Constructor público requerido por JPA y por el adaptador de persistencia. */
    /**
     * Constructor sin argumentos requerido por JPA para crear la entidad al leerla de la base de datos.
     */
    public WeatherReadingEntity() {}

    /**
     * Asigna la ciudad relacionada y los valores meteorológicos a la entidad. Se reutiliza tanto al insertar como al actualizar una medición.
     */
    public void updateFrom(CityEntity city, Double temperature, Double humidity,
                           Double windSpeed, LocalDateTime measuredAt, LocalDateTime queriedAt) {
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.measuredAt = measuredAt;
        this.queriedAt = queriedAt;
    }

    /**
     * Devuelve la clave primaria de la medición.
     */
    public Long getId() { return id; }
    /**
     * Devuelve la entidad CityEntity asociada mediante la clave foránea city_id.
     */
    public CityEntity getCity() { return city; }
    /**
     * Devuelve la temperatura persistida.
     */
    public Double getTemperature() { return temperature; }
    /**
     * Devuelve la humedad relativa persistida.
     */
    public Double getHumidity() { return humidity; }
    /**
     * Devuelve la velocidad del viento persistida.
     */
    public Double getWindSpeed() { return windSpeed; }
    /**
     * Devuelve el instante al que corresponde la observación meteorológica.
     */
    public LocalDateTime getMeasuredAt() { return measuredAt; }
    /**
     * Devuelve el instante en que ClimateAtlas consultó al proveedor.
     */
    public LocalDateTime getQueriedAt() { return queriedAt; }
}
