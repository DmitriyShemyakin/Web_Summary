package com.example.web_summaryy.dto.shift;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndShiftRequest {

    /**
     * Генерировать ли Excel отчет
     * По умолчанию - true
     */
    @Builder.Default
    private Boolean generateReport = true;

    private String comment;
}



