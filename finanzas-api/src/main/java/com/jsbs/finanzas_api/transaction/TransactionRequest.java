package com.jsbs.finanzas_api.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequest(
        @NotNull(message = "El importe es obligatorio")
        @Positive(message = "El importe debe ser mayor que cero")
        BigDecimal amount,

        @NotBlank(message = "La descripción es obligatoria")
        String description,

        @NotNull(message = "La fecha es obligatoria")
        LocalDateTime date,

        @NotNull(message = "La categoría es obligatoria")
        Long categoryId
) {
}
