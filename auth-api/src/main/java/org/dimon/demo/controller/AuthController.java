package org.dimon.demo.controller;

import org.dimon.demo.dto.RegisterRequest;
import org.dimon.demo.dto.LoginRequest;
import org.dimon.demo.dto.AuthResponse;
import org.dimon.demo.service.AuthService;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Validated @RequestBody RegisterRequest rq) {
        authService.register(rq);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginRequest rq) {
        String token = authService.login(rq);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}