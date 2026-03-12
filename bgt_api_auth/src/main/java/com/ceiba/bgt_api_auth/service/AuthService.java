package com.ceiba.bgt_api_auth.service;

import com.ceiba.bgt_api_auth.model.AuthResponse;
import com.ceiba.bgt_api_auth.model.LoginRequest;
import com.ceiba.bgt_api_auth.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
                .filter(customer -> {
                    System.out.println(customer);
                    return passwordEncoder.matches(request.getPassword(), customer.getPassUser());
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Credenciales inválidas")))
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
