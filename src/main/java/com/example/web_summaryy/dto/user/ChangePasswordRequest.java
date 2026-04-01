package com.example.web_summaryy.dto.user;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

/**
 * DTO для смены пароля
 * Используется в POST /api/users/{id}/change-password
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {

    private String newPassword;

}

