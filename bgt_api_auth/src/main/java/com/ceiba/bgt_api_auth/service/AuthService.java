package com.ceiba.bgt_api_auth.service;

import com.ceiba.bgt_api_auth.model.AuthResponse;
import com.ceiba.bgt_api_auth.model.LoginRequest;
import com.ceiba.bgt_api_auth.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Autentica al usuario y devuelve un AuthResponse con el JWT generado.
     * Lanza excepción si las credenciales son inválidas.
     */
    public Mono<AuthResponse> login(LoginRequest request) {
        return customerRepository.findByUsername(request.getUsername())
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado: " + request.getUsername())))
                .filter(customer -> {
                    log.debug("Customer encontrado: {}", customer.getUsername());
                    log.debug("passUser almacenado: {}", customer.getPassUser());
                    boolean matches = passwordEncoder.matches(request.getPassword(), customer.getPassUser());
                    log.debug("¿Contraseña coincide? {}", matches);
                    return matches;
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Contraseña incorrecta para: " + request.getUsername())))
                .map(customer -> {
                    String token = jwtService.generateToken(customer.getUsername());
                    return AuthResponse.builder()
                            .token(token)
                            .tokenType("Bearer")
                            .expiresIn(jwtService.getExpirationMs())
                            .username(customer.getUsername())
                            .build();
                });
    }
}
