package com.ceiba.bgt_api_customer.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import com.ceiba.bgt_api_customer.exception.ErrorMessages;
import com.ceiba.bgt_api_customer.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Servicio de validación de JWT en el microservicio de clientes.
 * Usa la misma clave secreta que bgt_api_auth para verificar los tokens.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrae el username (subject) del token.
     * Lanza UnauthorizedException si el token es inválido o está expirado.
     */
    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorMessages.INVALID_JWT_TOKEN, e);
        }
    }

    /**
     * Devuelve true si el token tiene firma válida y no ha expirado.
     */
    public boolean isTokenValid(String token) {
        try {
            Date expiry = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expiry != null && !expiry.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
