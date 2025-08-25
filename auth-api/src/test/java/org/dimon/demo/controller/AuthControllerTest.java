package org.dimon.demo.controller;

import org.dimon.demo.dto.AuthResponse;
import org.dimon.demo.dto.LoginRequest;
import org.dimon.demo.dto.RegisterRequest;
import org.dimon.demo.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("test@example.com", "password123");
        loginRequest = new LoginRequest("test@example.com", "password123");
    }

    @Test
    void register_ShouldReturnCreatedStatus() {
        // Arrange
        doNothing().when(authService).register(registerRequest);

        // Act
        ResponseEntity<Void> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void login_ShouldReturnToken() {
        // Arrange
        String expectedToken = "jwt-token-123";
        when(authService.login(loginRequest)).thenReturn(expectedToken);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);
        AuthResponse authResponse = response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(authResponse);
        assertEquals(expectedToken, authResponse.getToken());
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    void login_ShouldReturnValidResponse() {
        // Arrange
        String token = "test-jwt-token";
        when(authService.login(loginRequest)).thenReturn(token);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(token, response.getBody().getToken());
    }

    @Test
    void register_ShouldCallAuthService() {
        // Arrange
        doNothing().when(authService).register(registerRequest);

        // Act
        authController.register(registerRequest);

        // Assert
        verify(authService, times(1)).register(registerRequest);
    }
}