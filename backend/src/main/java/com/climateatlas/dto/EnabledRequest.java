package com.climateatlas.dto;

import jakarta.validation.constraints.NotNull;

/** Cuerpo de PATCH /api/cities/{id}/enabled. */
public record EnabledRequest(@NotNull Boolean enabled) {}
