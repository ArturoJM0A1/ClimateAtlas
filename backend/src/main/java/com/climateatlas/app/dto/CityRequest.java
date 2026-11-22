package com.climateatlas.app.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada REST: las restricciones HTTP no forman parte del dominio.
 *  * Las anotaciones validan el cuerpo antes de ejecutar el caso de uso.
 *  * @param name Nombre de la ciudad; no puede estar vacío y admite hasta 100 caracteres.
 *  * @param state Estado o entidad federativa; no puede estar vacío y admite hasta 100 caracteres.
 *  * @param latitude Latitud geográfica entre -90 y 90 grados.
 *  * @param longitude Longitud geográfica entre -180 y 180 grados.
 */
public record CityRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100) String state,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
) {}
