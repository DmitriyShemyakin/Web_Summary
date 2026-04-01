package com.example.web_summaryy.service.impl;

import com.example.web_summaryy.config.mapper.UserMapper;
import com.example.web_summaryy.dto.user.ChangePasswordRequest;
import com.example.web_summaryy.dto.user.CreateUserRequest;
import com.example.web_summaryy.dto.user.UpdateUserRequest;
import com.example.web_summaryy.dto.user.UserDtoResponse;
import com.example.web_summaryy.model.Direction;
import com.example.web_summaryy.model.Role;
import com.example.web_summaryy.model.User;
import com.example.web_summaryy.repository.DirectionRepository;
import com.example.web_summaryy.repository.RoleRepository;
import com.example.web_summaryy.repository.UserRepository;
import com.example.web_summaryy.service.CustomUserDetailsService;
import com.example.web_summaryy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DirectionRepository directionRepository;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;



    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository, RoleRepository roleRepository, DirectionRepository directionRepository, CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.directionRepository = directionRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userDetailsService.getUserByUsername(username);
    }

    @Override
    public UserDtoResponse createUser(CreateUserRequest request, User currentUser) {
        checkAdministratorRole(currentUser);
        validateCreateRequest(request);

        User user = userMapper.toEntity(request);
        user = userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDtoResponse updateUser(Long id, UpdateUserRequest request, User currentUser) {

        checkAdministratorRole(currentUser);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setIsActive(request.getIsActive());

        // Роли
        if (request.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));
            user.setObjRole(roles);
        }

        // Направления
        if (request.getDirectionIds() != null) {
            Set<Direction> directions = new HashSet<>(directionRepository.findAllById(request.getDirectionIds()));
            user.setDirections(directions);
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDtoResponse updateUserPassword(Long id, ChangePasswordRequest request, User currentUser) {
        checkAdministratorRole(currentUser);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String hashedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(hashedPassword);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }


    @Override
    public UserDtoResponse getUserById(Long id, User currentUser) {
        return null;
    }

    @Override
    public List<UserDtoResponse> getUsers(User currentUser) {
        checkAdministratorRole(currentUser);

        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }


    private void checkAdministratorRole(User currentUser) {
        if (!hasAdministratorRole(currentUser)) {
            throw new RuntimeException("Текущий пользователь не является администратором");
        }
    }

    @Override
    public boolean hasAdministratorRole(User currentUser) {
        return objRoleMatchesAnyNormalizedTitle(currentUser,
                "admin", "administrator", "администратор");
    }

    @Override
    public boolean seesAllIncidentsByObjRoles(User user) {
        return hasAdministratorRole(user)
                || objRoleMatchesAnyNormalizedTitle(user,
                "senior_duty", "seniorduty", "sdd", "старший_дежурный")
                || objRoleMatchesAnyNormalizedTitle(user,
                "duty_transport", "dutytransport", "дежурный_транспорта");
    }

    /**
     * Сравнение по {@code Role.title}: без учёта регистра, пробелы как в подчёркиваниях.
     */
    private static String normalizeRoleTitle(String title) {
        if (title == null) {
            return "";
        }
        return title.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
    }

    private static boolean objRoleMatchesAnyNormalizedTitle(User user, String... normalizedTitles) {
        if (user.getObjRole() == null || user.getObjRole().isEmpty()) {
            return false;
        }
        Set<String> targets = Arrays.stream(normalizedTitles)
                .map(t -> t.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        return user.getObjRole().stream()
                .map(r -> normalizeRoleTitle(r.getTitle()))
                .anyMatch(targets::contains);
    }

    private void validateCreateRequest(CreateUserRequest request) {
        if (request.getUsername() == null) {
            throw new RuntimeException("Не указан логин пользователя");
        }

        if (request.getPassword() == null) {
            throw new RuntimeException("Не указан пароль пользователя");
        }


        if (request.getFullName() == null) {
            throw new RuntimeException("Не указано имя пользователя");
        }

        if (request.getRoleIds() == null || request.getRoleIds().isEmpty()) {
            throw new RuntimeException("Не выбрана роль пользователя");
        }

    }

}
