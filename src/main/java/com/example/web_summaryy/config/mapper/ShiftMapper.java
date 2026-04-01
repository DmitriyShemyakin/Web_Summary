package com.example.web_summaryy.config.mapper;

import com.example.web_summaryy.dto.shift.ShiftDtoResponse;
import com.example.web_summaryy.model.Shift;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ShiftMapper {

    public ShiftDtoResponse toDto(Shift shift) {
        if (shift == null) {
            return null;
        }

        ShiftDtoResponse dto = ShiftDtoResponse.builder()
                .id(shift.getId())
                .startedAt(shift.getStartedAt())
                .endedAt(shift.getEndedAt())
                .excelFilePath(shift.getExcelFilePath())
                .status(shift.getStatus())
                .build();

        if (shift.getDutyUser() != null) {
            dto.setDutyUserId(shift.getDutyUser().getId());
            dto.setDutyUserName(shift.getDutyUser().getUsername());
            dto.setDutyUserFullName(shift.getDutyUser().getFullName());
        }

        if (shift.getStartedAt() != null) {
            if (shift.getEndedAt() != null) {
                Duration duration = Duration.between(shift.getStartedAt(), shift.getEndedAt());
                dto.setDurationMinutes(duration.toMinutes());
            } else {
                Duration duration = Duration.between(shift.getStartedAt(), java.time.LocalDateTime.now());
                dto.setDurationMinutes(duration.toMinutes());
            }
        }

        return dto;
    }
}
