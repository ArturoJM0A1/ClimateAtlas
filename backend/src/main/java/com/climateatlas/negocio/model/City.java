package com.climateatlas.negocio.model;

/**
 * Modelo de negocio de una ciudad.
 * No contiene anotaciones JPA ni depende de Spring: representa el dominio.
 */
public class City {
    private final Long id;
    private String name;
    private String state;
    private Double latitude;
    private Double longitude;
    private Boolean enabled;

    public City(String name, String state, Double latitude, Double longitude, Boolean enabled) {
        this(null, name, state, latitude, longitude, enabled);
    }

    public City(Long id, String name, String state, Double latitude,
                Double longitude, Boolean enabled) {
        this.id = id;
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
}
