package com.example.web_summaryy.dto.position;

import com.example.web_summaryy.dto.baseStation.BaseStationDtoResponse;
import com.example.web_summaryy.dto.ownerTeam.OwnerTeamDtoResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionDtoResponse {
    private String positionUUID;
    private String positionTechnapr;
    private Double positionGradlong;
    private Double positionGradlat;
    private String positionTechcentre;
    private String positionNameforbs;
    private String addressStr;
    private List<BaseStationDtoResponse> baseStations;
    private List<OwnerTeamDtoResponse> ownerTeam_te;
}
