package com.ordertracker.auth.service;

import com.ordertracker.auth.dao.entity.User;
import com.ordertracker.auth.dao.repository.UserRepository;
import com.ordertracker.auth.dto.request.LoginRequest;
import com.ordertracker.auth.dto.request.RegisterRequest;
import com.ordertracker.auth.dto.response.AuthResponse;
import com.ordertracker.auth.mapper.AuthMapper;
import com.ordertracker.exception.UserAlreadyExistsException;
import com.ordertracker.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (repository.findByEmail(request.email()).isPresent()) {
            throw new UserAlreadyExistsException("Bu e-poçt ünvanı ilə artıq qeydiyyatdan keçilib.");
        }

        User user = authMapper.toUser(request, passwordEncoder);

        repository.save(user);

        String jwtToken = jwtUtil.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("İstifadəçi tapılmadı"));

        String jwtToken = jwtUtil.generateToken(user);
        return new AuthResponse(jwtToken);
    }
}