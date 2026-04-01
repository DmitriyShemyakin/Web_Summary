package com.example.web_summaryy.service;

import com.example.web_summaryy.dto.incident.IncidentDtoResponse;
import com.example.web_summaryy.dto.user.ChangePasswordRequest;
import com.example.web_summaryy.dto.user.CreateUserRequest;
import com.example.web_summaryy.dto.user.UpdateUserRequest;
import com.example.web_summaryy.dto.user.UserDtoResponse;
import com.example.web_summaryy.model.User;

import java.util.List;

public interface UserService {

    /**
     * Есть ли у пользователя роль администратора (по {@link com.example.web_summaryy.model.Role} в {@code objRole}).
     */
    boolean hasAdministratorRole(User currentUser);

    /**
     * Видит ли пользователь все аварии без фильтра по направлениям (админ / старший / дежурный транспорта).
     */
    boolean seesAllIncidentsByObjRoles(User user);

    User getCurrentUser();
    UserDtoResponse createUser(CreateUserRequest request, User currentUser);

    UserDtoResponse updateUser(Long id, UpdateUserRequest request, User currentUser);

    UserDtoResponse updateUserPassword(Long id, ChangePasswordRequest request, User currentUser);

    UserDtoResponse getUserById(Long id, User currentUser);

    List<UserDtoResponse> getUsers(User currentUser);

}
