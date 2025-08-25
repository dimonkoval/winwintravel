package org.dimon.demo.service;

import org.dimon.demo.dto.LoginRequest;
import org.dimon.demo.dto.RegisterRequest;
import org.dimon.demo.exception.InvalidCredentialsException;
import org.dimon.demo.exception.UserAlreadyExistsException;
import org.dimon.demo.model.User;
import org.dimon.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.dimon.demo.security.JwtUtil;
import java.util.Set;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterRequest rq) {
        if (userRepo.findByUsername(rq.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException(rq.getUsername());
        }
        User u = User.builder()
                .username(rq.getUsername())
                .password(encoder.encode(rq.getPassword()))
                .roles(Set.of("USER"))
                .build();
        userRepo.save(u);
    }

    public String login(LoginRequest rq) {
        User user = userRepo.findByUsername(rq.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid login"));
        if (!encoder.matches(rq.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return jwtUtil.generateToken(user.getUsername());
    }
}