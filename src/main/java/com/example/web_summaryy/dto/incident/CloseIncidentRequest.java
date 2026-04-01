package com.example.web_summaryy.dto.incident;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO для закрытия аварии
 * Используется в PATCH /api/incidents/{id}/close
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloseIncidentRequest {

    /** Дата и время окончания (обязательно при закрытии через API). */
    private LocalDateTime endedAt;

    // Финальное описание/комментарий при закрытии
    private String description;

    // Можно обновить временные поля при закрытии
    private String akbDuration;
    private String guDuration;
    private String connectionDowntime;
    private String bsTotalDowntime;
}

