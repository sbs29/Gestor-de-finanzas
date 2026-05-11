package com.jsbs.finanzas_api.category;

public record CategoryResponse(
        Long id,
        String name,
        CategoryType type
) {
}
