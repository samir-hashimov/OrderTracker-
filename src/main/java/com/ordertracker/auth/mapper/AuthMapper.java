package com.ordertracker.auth.mapper;

import com.ordertracker.auth.dao.entity.User;
import com.ordertracker.auth.dto.request.RegisterRequest;

import com.ordertracker.util.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public User toUser(RegisterRequest request, PasswordEncoder passwordEncoder) {
        return User.builder()
                .firstname(request.firstname())
                .lastname(request.lastname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
    }
}