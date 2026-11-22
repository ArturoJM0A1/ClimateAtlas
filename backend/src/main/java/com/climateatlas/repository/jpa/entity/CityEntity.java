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

    /** Constructor público requerido por JPA y por el adaptador de persistencia. */
    /**
     * Constructor sin argumentos requerido por JPA para materializar objetos desde la base de datos.
     */
    public CityEntity() {}

    /**
     * Copia los atributos del modelo de negocio a esta entidad persistente. No asigna el id porque lo administra la base de datos.
     */
    public void updateFrom(String name, String state, Double latitude,
                           Double longitude, Boolean enabled) {
        this.name = name;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.enabled = enabled;
    }

    /**
     * Devuelve la clave primaria generada para la ciudad.
     */
    public Long getId() { return id; }
    /**
     * Devuelve el nombre almacenado en la columna name.
     */
    public String getName() { return name; }
    /**
     * Devuelve el estado almacenado en la columna state.
     */
    public String getState() { return state; }
    /**
     * Devuelve la latitud persistida.
     */
    public Double getLatitude() { return latitude; }
    /**
     * Devuelve la longitud persistida.
     */
    public Double getLongitude() { return longitude; }
    /**
     * Devuelve el indicador de habilitación persistido.
     */
    public Boolean getEnabled() { return enabled; }
    /**
     * Devuelve la colección de mediciones asociadas. La relación está controlada por el atributo city de WeatherReadingEntity.
     */
    public List<WeatherReadingEntity> getReadings() { return readings; }
}
