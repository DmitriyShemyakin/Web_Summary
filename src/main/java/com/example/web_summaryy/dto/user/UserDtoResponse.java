package com.example.web_summaryy.dto.user;

import com.example.web_summaryy.dto.direction.DirectionDtoResponse;
import com.example.web_summaryy.dto.role.RoleDtoResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDtoResponse {

    private Long id;

    private String username;
    private String fullName;
    private String email;
    private String phone;

    @Builder.Default
    private Set<RoleDtoResponse> roles = new HashSet<>();

    @Builder.Default
    private Set<DirectionDtoResponse> directions = new HashSet<>();

    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
}
