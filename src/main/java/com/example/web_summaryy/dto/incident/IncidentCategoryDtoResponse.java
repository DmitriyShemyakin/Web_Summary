package com.example.web_summaryy.dto.incident;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentCategoryDtoResponse {
    private Long id;
    private String categoryName;
    private String description;
}
