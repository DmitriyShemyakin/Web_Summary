package com.example.web_summaryy.dto.incident;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateIncidentRequest {

    private String incidentNumber;
    private String incidentNumberUrl;
    private String equipmentType;

    private Long incidentTypeId;
    private Long incidentCategoryId;
    private Long networkTypeId;

    private Integer positionLevel;

    private Set<Long> positionIds;

    private String baseStationsCount;

    private LocalDateTime startedAt;
    private String akbDuration;
    private String guDuration;
    private String connectionDowntime;
    private String bsTotalDowntime;

    private String notificationText;
    private String description;
}
