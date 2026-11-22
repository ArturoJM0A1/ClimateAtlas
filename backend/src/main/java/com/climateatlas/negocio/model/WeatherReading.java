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

    /**
     * Construye una medición con la ciudad, sus variables meteorológicas y dos marcas de tiempo: cuándo se midió y cuándo se consultó.
     */
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

    /**
     * Actualiza los valores meteorológicos y el momento de consulta de una medición existente. Conserva el id, la ciudad y measuredAt para no cambiar la identidad temporal del registro.
     */
    public void updateMeasurement(Double temperature, Double humidity,
                                  Double windSpeed, LocalDateTime queriedAt) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.queriedAt = queriedAt;
    }

    /**
     * Devuelve el identificador de la medición persistida.
     */
    public Long getId() { return id; }
    /**
     * Devuelve la ciudad a la que pertenece esta medición.
     */
    public City getCity() { return city; }
    /**
     * Devuelve la temperatura medida.
     */
    public Double getTemperature() { return temperature; }
    /**
     * Devuelve la humedad relativa medida.
     */
    public Double getHumidity() { return humidity; }
    /**
     * Devuelve la velocidad del viento medida.
     */
    public Double getWindSpeed() { return windSpeed; }
    /**
     * Devuelve el instante al que corresponden los datos entregados por el proveedor.
     */
    public LocalDateTime getMeasuredAt() { return measuredAt; }
    /**
     * Devuelve el instante en que ClimateAtlas consultó al proveedor.
     */
    public LocalDateTime getQueriedAt() { return queriedAt; }
}
