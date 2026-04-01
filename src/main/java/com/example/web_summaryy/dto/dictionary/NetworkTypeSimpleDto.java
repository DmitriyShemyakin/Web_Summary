package com.example.web_summaryy.dto.dictionary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NetworkTypeSimpleDto {
    private Long id;
    private String title;
    private String code;
}
