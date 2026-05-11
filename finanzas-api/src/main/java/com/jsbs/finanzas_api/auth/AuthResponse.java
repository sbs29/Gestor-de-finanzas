package com.jsbs.finanzas_api.auth;

public record AuthResponse(
        Long id,
        String name,
        String email,
        String role
) {
}
