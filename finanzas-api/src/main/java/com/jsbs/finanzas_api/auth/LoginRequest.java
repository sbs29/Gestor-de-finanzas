package com.jsbs.finanzas_api.auth;

public record LoginRequest(
        String email,
        String password
) {
}
