package com.example.web_summaryy.dto.incident;

import com.example.web_summaryy.dto.direction.DirectionDtoResponse;
import com.example.web_summaryy.model.IncidentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentDtoResponse {

    private Long id;

    /** Смена закрытия (null у старых записей). */
    private Long shiftId;

    private String incidentNumber;
    private String incidentNumberUrl;
    private String equipmentType;

    private IncidentTypeDtoResponse incidentType;
    private IncidentCategoryDtoResponse incidentCategory;

    private Integer positionLevel;

    private Long networkTypeId;
    private String networkTypeName;

    @Builder.Default
    private List<DirectionDtoResponse> directions = new ArrayList<>();

    private Set<Long> positionIds;
    @Builder.Default
    private List<String> positionNames = new ArrayList<>();
    private String directionNames;
    private String techCentreNames;

    private String baseStationsCount;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String akbDuration;
    private String guDuration;
    private String connectionDowntime;
    private String bsTotalDowntime;

    private String notificationText;
    private String description;

    private IncidentStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;

    private String createdByName;
    private String closedByName;
    private String updatedByName;

    private String duration;
}
