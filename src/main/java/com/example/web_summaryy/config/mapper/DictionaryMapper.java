package com.example.web_summaryy.config.mapper;

import com.example.web_summaryy.dto.dictionary.*;
import com.example.web_summaryy.dto.direction.DirectionDtoResponse;
import com.example.web_summaryy.dto.role.RoleDtoResponse;
import com.example.web_summaryy.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DictionaryMapper {
    public PositionSimpleDto toPositionDto(Position position) {
        return PositionSimpleDto.builder()
                .id(position.getId())
                .positionUUID(position.getPositionUUID())
                .positionNameforbs(position.getPositionNameforbs())
                .addressStr(position.getAddressStr())
                .directionTitle(position.getPositionTechnapr() != null ? position.getPositionTechnapr().getTitle() : null)
                .build();
    }
    public RoleDtoResponse toRoleDto(Role role) {
        RoleDtoResponse dto = new RoleDtoResponse();
        dto.setId(role.getId());
        dto.setTitle(role.getTitle());

        dto.setDirections(
                role.getDirections().stream()
                        .map(d -> new DirectionDtoResponse(d.getId(), d.getTitle()))
                        .collect(Collectors.toList())
        );

        return dto;
    }


    public DirectionSimpleDto toDirectionDto(Direction direction) {
        return DirectionSimpleDto.builder()
                .id(direction.getId())
                .title(direction.getTitle())
                .build();
    }

    public IncidentTypeSimpleDto toIncidentTypeDto(IncidentType type) {
        return IncidentTypeSimpleDto.builder()
                .id(type.getId())
                .typeCode(type.getTypeCode())
                .isActive(type.getIsActive())
                .build();
    }

    public IncidentCategorySimpleDto toIncidentCategoryDto(IncidentCategory category) {
        return IncidentCategorySimpleDto.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .build();
    }

    public NetworkTypeSimpleDto toNetworkTypeDto(NetworkType networkType) {
        return NetworkTypeSimpleDto.builder()
                .id(networkType.getId())
                .title(networkType.getTitle())
                .code(networkType.getCode())
                .build();
    }

    public PositionDetailDtoResponse toPositionDetailDto(Position position) {
        if (position == null) {
            return null;
        }

        List<String> bsLabels = new ArrayList<>();
        if (position.getBaseStations() != null) {
            position.getBaseStations().forEach(bs -> {
                String label = bs.getBaseStationDispTitle();
                if (label == null || label.isBlank()) {
                    label = bs.getBaseStationName();
                }
                if (label != null && !label.isBlank()) {
                    bsLabels.add(label);
                }
            });
        }

        int bsCount = position.getBaseStations() != null ? position.getBaseStations().size() : 0;

        Direction dir = position.getPositionTechnapr();
        TechCentre tc = position.getPositionTechcentre();
        OwnerTeam ot = position.getOwnerTeam();

        return PositionDetailDtoResponse.builder()
                .id(position.getId())
                .positionUUID(position.getPositionUUID())
                .positionNameforbs(position.getPositionNameforbs())
                .addressStr(position.getAddressStr())
                .directionId(dir != null ? dir.getId() : null)
                .directionTitle(dir != null ? dir.getTitle() : null)
                .techCentreId(tc != null ? tc.getId() : null)
                .techCentreTitle(tc != null ? tc.getTitle() : null)
                .ownerTeamId(ot != null ? ot.getId() : null)
                .ownerTeamTitle(ot != null ? ot.getTitle() : null)
                .positionGradlat(position.getPositionGradlat())
                .positionGradlong(position.getPositionGradlong())
                .baseStationLabels(bsLabels)
                .baseStationsCount(bsCount)
                .build();
    }
}

