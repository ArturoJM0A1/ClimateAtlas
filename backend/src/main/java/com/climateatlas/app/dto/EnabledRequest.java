package com.climateatlas.app.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO de PATCH para cambiar la habilitación de una ciudad.
 * @param enabled Nuevo estado de habilitación; {@code @NotNull} impide solicitudes sin valor.
 */
public record EnabledRequest(@NotNull Boolean enabled) {}
