package com.kinal.biblioteca.dto;

import jakarta.validation.constraints.NotNull;

public record PrestamoRequest(@NotNull Long usuarioId, @NotNull Long libroId) {
}