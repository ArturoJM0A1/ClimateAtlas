package com.climateatlas.app.dto;

import jakarta.validation.constraints.NotNull;

/** DTO de PATCH para cambiar la habilitación de una ciudad. */
public record EnabledRequest(@NotNull Boolean enabled) {}
