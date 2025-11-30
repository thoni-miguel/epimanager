package com.thoni.epimanager.controller;

import com.thoni.epimanager.dto.AuthResponse;
import com.thoni.epimanager.dto.LoginRequest;
import com.thoni.epimanager.dto.RegisterRequest;
import com.thoni.epimanager.entity.User;
import com.thoni.epimanager.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        User user = authService.registerUser(request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário criado com sucesso: " + user.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Autentica usuário
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        // Gera token simples
        String token = authService.generateBasicToken(request.username());

        return ResponseEntity.ok(new AuthResponse(token, request.username()));
    }
}
