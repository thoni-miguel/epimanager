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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints públicos para registro e login de usuários")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar novo usuário", description = "Cria um novo usuário no sistema com senha hasheada (BCrypt)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário já existe")
    })
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        User user = authService.registerUser(request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário criado com sucesso: " + user.getUsername());
    }

    @PostMapping("/login")
    @Operation(summary = "Fazer login", description = "Autentica usuário e retorna token (informativo). Use Basic Auth nos endpoints protegidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Autentica usuário
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        // Gera token simples
        String token = authService.generateBasicToken(request.username());

        return ResponseEntity.ok(new AuthResponse(token, request.username()));
    }
}
