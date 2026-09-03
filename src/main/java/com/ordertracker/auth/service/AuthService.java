package com.ordertracker.auth.service;

import com.ordertracker.auth.dao.entity.User;
import com.ordertracker.auth.dao.repository.UserRepository;
import com.ordertracker.auth.dto.request.LoginRequest;
import com.ordertracker.auth.dto.request.RefreshTokenRequest;
import com.ordertracker.auth.dto.request.RegisterRequest;
import com.ordertracker.auth.dto.response.AuthResponse;
import com.ordertracker.auth.mapper.AuthMapper;
import com.ordertracker.exception.InvalidRefreshTokenException;
import com.ordertracker.exception.UserAlreadyExistsException;
import com.ordertracker.exception.UserNotFoundException;
import com.ordertracker.security.CustomUserDetails;
import com.ordertracker.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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

    public String register(RegisterRequest request) {
        if (repository.findByEmail(request.email()).isPresent()) {
            throw new UserAlreadyExistsException("Bu e-poçt ünvanı ilə artıq qeydiyyatdan keçilib.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = authMapper.toUser(request, encodedPassword);

        repository.save(user);

        return "İstifadəçi uğurla qeydiyyatdan keçdi! Zəhmət olmasa, daxil olun.";
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        String userEmail = jwtUtil.extractUsername(refreshToken);
        if (userEmail == null) {
            throw new InvalidRefreshTokenException("Refresh token etibarsızdır və ya vaxtı bitib!");
        }
        User user = repository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı"));
        UserDetails userDetails = new CustomUserDetails(user);
        if (!jwtUtil.isTokenValid(refreshToken, userDetails)) {
            throw new InvalidRefreshTokenException("Refresh token etibarsızdır və ya vaxtı bitib!");
        }
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        return new AuthResponse(newAccessToken, refreshToken);
    }
}