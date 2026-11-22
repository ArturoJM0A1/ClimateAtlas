package com.climateatlas.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ciudad que ClimateAtlas consulta en Open-Meteo.
 * Una instancia se persiste como una fila de cities. La colección readings
 * representa el lado One-to-Many y se carga solo cuando se solicita.
 */
@Entity
@Table(name = "cities",
        uniqueConstraints = @UniqueConstraint(name = "uk_city_name_state", columnNames = {"name", "state"}),
        indexes = @Index(name = "idx_city_enabled", columnList = "enabled"))
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Boolean enabled = true;

    @OneToMany(mappedBy = "city", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<WeatherReading> readings = new ArrayList<>();

    protected City() {
        // JPA utiliza este constructor para reconstruir entidades.
    }

    public City(String name, String state, Double latitude, Double longitude, Boolean enabled) {
        this.name = name;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.enabled = enabled;
    }

    public void updateDetails(String name, String state, Double latitude, Double longitude) {
        this.name = name;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getState() { return state; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Boolean getEnabled() { return enabled; }
    public List<WeatherReading> getReadings() { return readings; }
}
