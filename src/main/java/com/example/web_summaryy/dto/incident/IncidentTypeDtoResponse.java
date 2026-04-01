package com.example.web_summaryy.dto.incident;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentTypeDtoResponse {
    private Long id;
    private String typeCode;

}
