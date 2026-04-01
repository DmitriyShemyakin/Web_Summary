package com.example.web_summaryy.controller;

import com.example.web_summaryy.dto.user.ChangePasswordRequest;
import com.example.web_summaryy.dto.user.CreateUserRequest;
import com.example.web_summaryy.dto.user.UpdateUserRequest;
import com.example.web_summaryy.dto.user.UserDtoResponse;
import com.example.web_summaryy.model.User;
import com.example.web_summaryy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDtoResponse> createUser(@RequestBody CreateUserRequest request) {
        User currentUser = userService.getCurrentUser();
        UserDtoResponse user = userService.createUser(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping
    public ResponseEntity<List<UserDtoResponse>> getUsers() {
        User currentUser = userService.getCurrentUser();
        List<UserDtoResponse> users = userService.getUsers(currentUser);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<UserDtoResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {
        User currentUser = userService.getCurrentUser();
        UserDtoResponse user = userService.updateUser(id, request, currentUser);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/change-password/{id}")
    public ResponseEntity<UserDtoResponse> updateUserPassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
        User currentUser = userService.getCurrentUser();
        UserDtoResponse user = userService.updateUserPassword(id, request, currentUser);
        return ResponseEntity.ok(user);
    }


}
