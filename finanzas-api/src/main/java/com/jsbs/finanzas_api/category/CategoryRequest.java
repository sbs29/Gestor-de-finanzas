package com.jsbs.finanzas_api.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
        @NotBlank(message = "El nombre de la categoría es obligatorio")
        String name,
        @NotNull(message = "El tipo de categoría es obligatorio")
        CategoryType type
) {
}
