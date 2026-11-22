package com.climateatlas.repository.jpa.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Entidad JPA exclusiva de la capa repository. */
@Entity
@Table(name = "cities",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_city_name_state", columnNames = {"name", "state"}),
        indexes = @Index(name = "idx_city_enabled", columnList = "enabled"))
public class CityEntity {
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
    private List<WeatherReadingEntity> readings = new ArrayList<>();

    protected CityEntity() {
        // Constructor requerido por JPA.
    }

    public void updateFrom(String name, String state, Double latitude,
                           Double longitude, Boolean enabled) {
        this.name = name;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.enabled = enabled;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getState() { return state; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Boolean getEnabled() { return enabled; }
    public List<WeatherReadingEntity> getReadings() { return readings; }
}
