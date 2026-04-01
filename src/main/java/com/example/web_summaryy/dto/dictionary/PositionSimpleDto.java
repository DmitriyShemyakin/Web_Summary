package com.example.web_summaryy.dto.dictionary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PositionSimpleDto {
    private Long id;
    private String positionUUID;
    private String positionNameforbs;
    private String addressStr;
    private String directionTitle;
}


