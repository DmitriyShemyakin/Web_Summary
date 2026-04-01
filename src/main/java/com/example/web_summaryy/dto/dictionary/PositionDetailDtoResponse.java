package com.example.web_summaryy.dto.dictionary;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Расширенные данные позиции для формы аварии (после выбора из списка).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionDetailDtoResponse {

    private Long id;
    private String positionUUID;
    private String positionNameforbs;
    private String addressStr;

    private Long directionId;
    private String directionTitle;

    private Long techCentreId;
    private String techCentreTitle;

    private Long ownerTeamId;
    private String ownerTeamTitle;

    private Double positionGradlat;
    private Double positionGradlong;

    @Builder.Default
    private List<String> baseStationLabels = new ArrayList<>();

    private int baseStationsCount;
}
