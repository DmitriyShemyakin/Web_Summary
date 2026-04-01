package com.example.web_summaryy.dto.shift;

import com.example.web_summaryy.model.ShiftStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftDtoResponse {

    private Long id;

    private Long dutyUserId;
    private String dutyUserName;
    private String dutyUserFullName;

    private Long directionId;
    private String directionTitle;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private String excelFilePath;

    private ShiftStatus status;

    private Long durationMinutes; // Длительность смены в минутах
}





