package com.example.web_summaryy.config.mapper;

import com.example.web_summaryy.dto.incident.*;
import com.example.web_summaryy.model.*;
import com.example.web_summaryy.repository.IncidentCategoryRepository;
import com.example.web_summaryy.repository.IncidentTypeRepository;
import com.example.web_summaryy.repository.NetworkTypeRepository;
import com.example.web_summaryy.repository.PositionRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class IncidentMapper {

    private final IncidentTypeRepository incidentTypeRepository;
    private final IncidentCategoryRepository incidentCategoryRepository;
    private final PositionRepository positionRepository;
    private final NetworkTypeRepository networkTypeRepository;

    public IncidentMapper(IncidentTypeRepository incidentTypeRepository,
                         IncidentCategoryRepository incidentCategoryRepository,
                         PositionRepository positionRepository,
                         NetworkTypeRepository networkTypeRepository) {
        this.incidentTypeRepository = incidentTypeRepository;
        this.incidentCategoryRepository = incidentCategoryRepository;
        this.positionRepository = positionRepository;
        this.networkTypeRepository = networkTypeRepository;
    }

    public IncidentDtoResponse toDto(Incident incident) {
        if (incident == null) {
            return null;
        }

        Set<Position> positions = incident.getPositions();

        List<String> posNames = positions.stream()
                .map(p -> {
                    String name = p.getPositionNameforbs();
                    String addr = p.getAddressStr();
                    if (name == null) return null;
                    return addr != null && !addr.isBlank() ? name + " - " + addr : name;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Optional<Position> firstPos = positions.stream()
                .min(Comparator.comparingLong(Position::getId));

        String dirNames = firstPos
                .map(Position::getPositionTechnapr)
                .map(Direction::getTitle)
                .orElse(null);

        String tcNames = firstPos
                .map(Position::getPositionTechcentre)
                .map(TechCentre::getTitle)
                .orElse(null);

        NetworkType nt = incident.getNetworkType();

        return IncidentDtoResponse.builder()
                .id(incident.getId())
                .shiftId(incident.getShift() != null ? incident.getShift().getId() : null)
                .incidentNumber(incident.getIncidentNumber())
                .incidentNumberUrl(incident.getIncidentNumberUrl())
                .equipmentType(incident.getEquipmentType())
                .incidentType(toIncidentTypeDto(incident.getIncidentType()))
                .incidentCategory(toIncidentCategoryDto(incident.getIncidentCategory()))
                .positionLevel(incident.getPositionLevel())
                .networkTypeId(nt != null ? nt.getId() : null)
                .networkTypeName(nt != null ? nt.getTitle() : null)
                .positionIds(positions.stream()
                        .map(Position::getId)
                        .collect(Collectors.toSet()))
                .positionNames(posNames)
                .directionNames(dirNames)
                .techCentreNames(tcNames)
                .baseStationsCount(incident.getBaseStationsCount())
                .startedAt(incident.getStartedAt())
                .endedAt(incident.getEndedAt())
                .akbDuration(incident.getAkbDuration())
                .guDuration(incident.getGuDuration())
                .connectionDowntime(incident.getConnectionDowntime())
                .bsTotalDowntime(incident.getBsTotalDowntime())
                .notificationText(incident.getNotificationText())
                .description(incident.getDescription())
                .status(incident.getStatus())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .closedAt(incident.getClosedAt())
                .createdByName(incident.getCreatedBy() != null ? incident.getCreatedBy().getFullName() : null)
                .closedByName(incident.getClosedBy() != null ? incident.getClosedBy().getFullName() : null)
                .updatedByName(incident.getUpdatedBy() != null ? incident.getUpdatedBy().getFullName() : null)
                .duration(incident.calculateDuration())
                .build();
    }

    /**
     * {@code shift_id} проставляется при закрытии аварии.
     */
    public Incident toEntity(CreateIncidentRequest request, User currentUser) {
        if (request == null) {
            return null;
        }

        Incident incident = Incident.builder()
                .incidentNumber(request.getIncidentNumber())
                .incidentNumberUrl(request.getIncidentNumberUrl())
                .equipmentType(nullIfBlank(request.getEquipmentType()))
                .baseStationsCount(request.getBaseStationsCount())
                .startedAt(request.getStartedAt())
                .akbDuration(request.getAkbDuration())
                .guDuration(request.getGuDuration())
                .connectionDowntime(request.getConnectionDowntime())
                .bsTotalDowntime(request.getBsTotalDowntime())
                .notificationText(request.getNotificationText())
                .description(request.getDescription())
                .positionLevel(request.getPositionLevel())
                .status(IncidentStatus.OPEN)
                .createdBy(currentUser)
                .positions(new HashSet<>())
                .build();

        if (request.getIncidentTypeId() != null) {
            incidentTypeRepository.findById(request.getIncidentTypeId())
                    .ifPresent(incident::setIncidentType);
        }

        if (request.getIncidentCategoryId() != null) {
            incidentCategoryRepository.findById(request.getIncidentCategoryId())
                    .ifPresent(incident::setIncidentCategory);
        }

        if (request.getNetworkTypeId() != null) {
            networkTypeRepository.findById(request.getNetworkTypeId())
                    .ifPresent(incident::setNetworkType);
        }

        if (request.getPositionIds() != null && !request.getPositionIds().isEmpty()) {
            Set<Position> positions = new HashSet<>(positionRepository.findAllById(request.getPositionIds()));
            incident.setPositions(positions);

            if (request.getBaseStationsCount() == null || request.getBaseStationsCount().isEmpty()) {
                incident.setBaseStationsCount(String.valueOf(positions.size()));
            }
        }

        return incident;
    }

    public void updateEntity(Incident incident, UpdateIncidentRequest request, User currentUser) {
        if (request == null || incident == null) {
            return;
        }

        if (request.getIncidentNumber() != null) {
            incident.setIncidentNumber(request.getIncidentNumber());
        }
        if (request.getIncidentNumberUrl() != null) {
            incident.setIncidentNumberUrl(request.getIncidentNumberUrl());
        }
        if (request.getEquipmentType() != null) {
            incident.setEquipmentType(nullIfBlank(request.getEquipmentType()));
        }
        if (request.getIncidentTypeId() != null) {
            incidentTypeRepository.findById(request.getIncidentTypeId())
                    .ifPresent(incident::setIncidentType);
        }
        if (request.getIncidentCategoryId() != null) {
            incidentCategoryRepository.findById(request.getIncidentCategoryId())
                    .ifPresent(incident::setIncidentCategory);
        }
        if (request.getNetworkTypeId() != null) {
            networkTypeRepository.findById(request.getNetworkTypeId())
                    .ifPresent(incident::setNetworkType);
        }
        incident.setPositionLevel(request.getPositionLevel());
        if (request.getPositionIds() != null) {
            Set<Position> positions = new HashSet<>(positionRepository.findAllById(request.getPositionIds()));
            incident.setPositions(positions);

            if (request.getBaseStationsCount() == null || request.getBaseStationsCount().isEmpty()) {
                incident.setBaseStationsCount(String.valueOf(positions.size()));
            }
        }
        if (request.getBaseStationsCount() != null) {
            incident.setBaseStationsCount(request.getBaseStationsCount());
        }
        if (request.getStartedAt() != null) {
            incident.setStartedAt(request.getStartedAt());
        }
        if (request.getEndedAt() != null) {
            LocalDateTime start = incident.getStartedAt();
            if (start != null && request.getEndedAt().isBefore(start)) {
                throw new IllegalArgumentException("Окончание не может быть раньше начала");
            }
            incident.setEndedAt(request.getEndedAt());
        }
        if (request.getAkbDuration() != null) {
            incident.setAkbDuration(request.getAkbDuration());
        }
        if (request.getGuDuration() != null) {
            incident.setGuDuration(request.getGuDuration());
        }
        if (request.getConnectionDowntime() != null) {
            incident.setConnectionDowntime(request.getConnectionDowntime());
        }
        if (request.getBsTotalDowntime() != null) {
            incident.setBsTotalDowntime(request.getBsTotalDowntime());
        }
        if (request.getNotificationText() != null) {
            incident.setNotificationText(request.getNotificationText());
        }
        if (request.getDescription() != null) {
            incident.setDescription(request.getDescription());
        }

        incident.setUpdatedBy(currentUser);
    }

    public void closeEntity(Incident incident, CloseIncidentRequest request, User currentUser, Shift shift) {
        if (request == null || incident == null || shift == null) {
            return;
        }

        if (request.getEndedAt() != null) {
            incident.setEndedAt(request.getEndedAt());
        }

        if (request.getAkbDuration() != null) {
            incident.setAkbDuration(request.getAkbDuration());
        }
        if (request.getGuDuration() != null) {
            incident.setGuDuration(request.getGuDuration());
        }
        if (request.getConnectionDowntime() != null) {
            incident.setConnectionDowntime(request.getConnectionDowntime());
        }
        if (request.getBsTotalDowntime() != null) {
            incident.setBsTotalDowntime(request.getBsTotalDowntime());
        }

        if (request.getDescription() != null) {
            incident.setDescription(request.getDescription());
        }

        incident.close();
        incident.setClosedBy(currentUser);
        incident.setShift(shift);
    }

    public IncidentTypeDtoResponse toIncidentTypeDto(IncidentType incidentType) {
        if (incidentType == null) {
            return null;
        }
        return IncidentTypeDtoResponse.builder()
                .id(incidentType.getId())
                .typeCode(incidentType.getTypeCode())
                .build();
    }

    private static String nullIfBlank(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    public IncidentCategoryDtoResponse toIncidentCategoryDto(IncidentCategory incidentCategory) {
        if (incidentCategory == null) {
            return null;
        }
        return IncidentCategoryDtoResponse.builder()
                .id(incidentCategory.getId())
                .categoryName(incidentCategory.getCategoryName())
                .description(incidentCategory.getDescription())
                .build();
    }
}

