package com.example.web_summaryy.dto.ownerTeam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerTeamDtoResponse {
    @JsonProperty("id")
    private String id;

    private String title;
}
