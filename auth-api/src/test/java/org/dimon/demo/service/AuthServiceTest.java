package org.dimon.demo.service;

import org.dimon.demo.dto.LoginRequest;
import org.dimon.demo.dto.RegisterRequest;
import org.dimon.demo.exception.InvalidCredentialsException;
import org.dimon.demo.exception.UserAlreadyExistsException;
import org.dimon.demo.model.User;
import org.dimon.demo.repository.UserRepository;
import org.dimon.demo.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_shouldThrowIfUserExists() {
        RegisterRequest request = new RegisterRequest("john", "pass");
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void register_shouldSaveUserIfNotExists() {
        RegisterRequest request = new RegisterRequest("john", "pass");
        when(userRepo.findByUsername("john")).thenReturn(Optional.empty());
        when(encoder.encode("pass")).thenReturn("hashed");

        authService.register(request);

        verify(userRepo).save(argThat(user ->
                user.getUsername().equals("john") &&
                        user.getPassword().equals("hashed") &&
                        user.getRoles().contains("USER")
        ));
    }

    @Test
    void login_shouldThrowIfUserNotFound() {
        LoginRequest request =
                new LoginRequest("john", "pass");
        when(userRepo.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void login_shouldThrowIfPasswordNotMatches() {
        LoginRequest request = new LoginRequest("john", "pass");
        User user = User.builder().username("john").password("hashed").build();

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
        when(encoder.matches("pass", "hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_shouldReturnTokenIfSuccess() {
        LoginRequest request = new LoginRequest("john", "pass");
        User user = User.builder().username("john").password("hashed").build();

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
        when(encoder.matches("pass", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken("john")).thenReturn("token123");

        String token = authService.login(request);

        assertEquals("token123", token);
    }
}
