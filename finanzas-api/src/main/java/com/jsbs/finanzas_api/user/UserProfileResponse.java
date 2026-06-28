package com.jsbs.finanzas_api.user;

public record UserProfileResponse(

        Long id,
        String name,
        String email,
        String role

) {
}
