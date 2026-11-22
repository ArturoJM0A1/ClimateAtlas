package com.climateatlas.app.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** DTO de entrada REST: las restricciones HTTP no forman parte del dominio. */
public record CityRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100) String state,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
) {}
