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

    /**
     * Construye una ciudad nueva sin id asignado; el identificador se establece al guardarla.
     */
    public City(String name, String state, Double latitude, Double longitude, Boolean enabled) {
        this(null, name, state, latitude, longitude, enabled);
    }

    /**
     * Reconstruye una ciudad que ya tiene id, por ejemplo al convertir una entidad persistente al modelo de negocio.
     */
    public City(Long id, String name, String state, Double latitude,
                Double longitude, Boolean enabled) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.enabled = enabled;
    }

    /**
     * Actualiza los datos descriptivos y geográficos de la ciudad. No modifica el id ni el estado enabled.
     */
    public void updateDetails(String name, String state, Double latitude, Double longitude) {
        this.name = name;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Cambia si la ciudad participa en las consultas meteorológicas programadas.
     */
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    /**
     * Devuelve el identificador de la ciudad; es null antes de guardarse por primera vez.
     */
    public Long getId() { return id; }
    /**
     * Devuelve el nombre de la ciudad.
     */
    public String getName() { return name; }
    /**
     * Devuelve el estado o entidad federativa de la ciudad.
     */
    public String getState() { return state; }
    /**
     * Devuelve la latitud geográfica usada para consultar el clima.
     */
    public Double getLatitude() { return latitude; }
    /**
     * Devuelve la longitud geográfica usada para consultar el clima.
     */
    public Double getLongitude() { return longitude; }
    /**
     * Indica si la ciudad está habilitada para las actualizaciones meteorológicas.
     */
    public Boolean getEnabled() { return enabled; }
}
