package com.example.web_summaryy.dto.auth;

import com.example.web_summaryy.dto.direction.DirectionDtoResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String fullName;
    private String role;
    private List<DirectionDtoResponse> directions;
}



