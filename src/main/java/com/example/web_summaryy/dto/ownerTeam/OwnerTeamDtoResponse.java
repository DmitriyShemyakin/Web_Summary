package com.example.web_summaryy.dto.ownerTeam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerTeamDtoResponse {
    @JsonProperty("id")  // В JSON приходит как "id", а не "externalId"
    private String id;

    private String title;
}
