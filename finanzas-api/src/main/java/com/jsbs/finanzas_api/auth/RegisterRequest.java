package com.jsbs.finanzas_api.auth;

public record RegisterRequest(
        String name,
        String email,
        String password
) {
}
