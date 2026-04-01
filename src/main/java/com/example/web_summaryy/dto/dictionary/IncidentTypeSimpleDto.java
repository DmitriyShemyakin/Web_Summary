package com.example.web_summaryy.dto.dictionary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IncidentTypeSimpleDto {
    private Long id;
    private String typeCode;
    private String typeName;
    private Boolean isActive;
}


