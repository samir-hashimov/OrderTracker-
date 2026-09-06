package com.ordertracker.auth.service;

import com.ordertracker.auth.dao.entity.User;
import com.ordertracker.auth.dao.repository.UserRepository;
import com.ordertracker.exception.InvalidRoleException;
import com.ordertracker.exception.UserNotFoundException;
import com.ordertracker.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    @Transactional
    public String changeUserRole(Long targetUserId, Role newRole) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı!"));

        if (user.getRole() == newRole) {
            throw new InvalidRoleException("İstifadəçi onsuz da bu roldadır!");
        }

        user.setRole(newRole);

        userRepository.save(user);

        return String.format(
                "İstifadəçi '%s %s' uğurla %s roluna keçirildi.",
                user.getFirstname(),
                user.getLastname(),
                newRole.name());
    }
}