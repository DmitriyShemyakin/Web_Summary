package com.example.web_summaryy.config.mapper;

import com.example.web_summaryy.dto.direction.DirectionDtoResponse;
import com.example.web_summaryy.dto.role.RoleDtoResponse;
import com.example.web_summaryy.dto.user.CreateUserRequest;
import com.example.web_summaryy.dto.user.UpdateUserRequest;
import com.example.web_summaryy.dto.user.UserDtoResponse;
import com.example.web_summaryy.model.Direction;
import com.example.web_summaryy.model.Role;
import com.example.web_summaryy.model.User;
import com.example.web_summaryy.repository.DirectionRepository;
import com.example.web_summaryy.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final RoleRepository roleRepository;
    private final DirectionRepository directionRepository;
    private final PasswordEncoder passwordEncoder;

    public UserMapper(RoleRepository roleRepository,
                     DirectionRepository directionRepository,
                     PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.directionRepository = directionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDtoResponse toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDtoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(user.getObjRole() != null ?
                        user.getObjRole().stream()
                                .map(this::toRoleDto)
                                .collect(Collectors.toSet()) :
                        new HashSet<>())
                .directions(user.getDirections() != null ?
                        user.getDirections().stream()
                                .map(this::toDirectionDto)
                                .collect(Collectors.toSet()) :
                        new HashSet<>())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    public User toEntity(CreateUserRequest request) {
        if (request == null) {
            return null;
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .username(request.getUsername())
                .password(hashedPassword)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .isActive(true)
                .objRole(new HashSet<>())
                .directions(new HashSet<>())
                .build();

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));
            user.setObjRole(roles);
        }

        if (request.getDirectionIds() != null && !request.getDirectionIds().isEmpty()) {
            Set<Direction> directions = new HashSet<>(directionRepository.findAllById(request.getDirectionIds()));
            user.setDirections(directions);
        }

        return user;
    }

    public void updateEntity(User user, UpdateUserRequest request) {
        if (request == null || user == null) {
            return;
        }

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        if (request.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));
            user.setObjRole(roles);
        }

        if (request.getDirectionIds() != null) {
            Set<Direction> directions = new HashSet<>(directionRepository.findAllById(request.getDirectionIds()));
            user.setDirections(directions);
        }
    }

    public RoleDtoResponse toRoleDto(Role role) {
        if (role == null) {
            return null;
        }
        return RoleDtoResponse.builder()
                .id(role.getId())
                .title(role.getTitle())
                .build();
    }

    public DirectionDtoResponse toDirectionDto(Direction direction) {
        if (direction == null) {
            return null;
        }
        return DirectionDtoResponse.builder()
                .id(direction.getId())
                .title(direction.getTitle())
                .build();
    }
}
