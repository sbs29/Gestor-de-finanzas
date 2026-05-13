package com.jsbs.finanzas_api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "gXdQW3kqimvCFgfXPKfNhwNUNf6lhNPz\n" +
            "KN1EpYrftPiFSTQJAKWRySmjjiDb1saJ\n" +
            "BcPC0SvMFQPsa1Z1rwdfaAlYOjjQ6Xqb\n" +
            "G4cIQfCPVxoBBeiV3ZdHPNdrSF8SNvi8\n" +
            "ZepELSWgETEMjKtLjyk8YUFwkE393ueQ\n" +
            "HI9GcvLy8j7L44SlHrKJH5i9gEYE4U4e\n" +
            "0JzLD1kgKvcFIgUYbWKX2x2eTAPYG8Qe\n" +
            "eLAs4W5vjYYs7DE8LwZQT1EXHv6nVEZy\n" +
            "WHGFvp2PoJcEchhRMkFgHt4zFWrA17m9\n" +
            "4oTlLwJfn5bhB8e2qmE1GFeHyXImwO4J";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String email) {
        long expirationTime = 1000 * 60 * 60;

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        String username = extractUsername(token);

        return username.equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

}
