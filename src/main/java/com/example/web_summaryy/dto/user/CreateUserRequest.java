package com.example.web_summaryy.dto.user;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;

    private Set<Long> roleIds;

    private Set<Long> directionIds;
}
