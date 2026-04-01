package com.example.web_summaryy.dto.dictionary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IncidentCategorySimpleDto {
    private Long id;
    private String categoryName;
    private String description;
    private Boolean isActive;
}


