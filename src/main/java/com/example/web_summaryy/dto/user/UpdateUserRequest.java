package com.example.web_summaryy.dto.user;

import lombok.*;

import java.util.Set;

/** Поля null в теле не меняют соответствующие поля сущности (кроме явно переданных коллекций). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    private String username;
    private String fullName;
    private String email;
    private String phone;

    private Set<Long> roleIds;
    private Set<Long> directionIds;
    private Boolean isActive;
}
