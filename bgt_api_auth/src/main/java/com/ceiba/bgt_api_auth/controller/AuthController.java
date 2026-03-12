package com.ceiba.bgt_api_auth.controller;

import com.ceiba.bgt_api_auth.model.AuthResponse;
import com.ceiba.bgt_api_auth.model.LoginRequest;
import com.ceiba.bgt_api_auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    /**
     * POST /auth/login
     * Body: { "username": "...", "password": "..." }
     * Response: AuthResponse con el JWT
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody LoginRequest request) {
        return authService.login(request)
                .map(ResponseEntity::ok)
                .onErrorResume(RuntimeException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }
}
