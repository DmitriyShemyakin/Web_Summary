package com.example.web_summaryy.service.impl;

import com.example.web_summaryy.dto.baseStation.BaseStationDtoResponse;
import com.example.web_summaryy.dto.ownerTeam.OwnerTeamDtoResponse;
import com.example.web_summaryy.dto.position.PositionDtoResponse;
import com.example.web_summaryy.model.*;
import com.example.web_summaryy.repository.DirectionRepository;
import com.example.web_summaryy.repository.OwnerTeamRepository;
import com.example.web_summaryy.repository.PositionRepository;
import com.example.web_summaryy.repository.TechCentreRepository;
import com.example.web_summaryy.service.ApiService;
import com.example.web_summaryy.service.UpdateService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class UpdateServiceImpl implements UpdateService {
    private static final int BATCH_SIZE = 100;

    private final PositionRepository positionRepository;
    private final DirectionRepository directionRepository;
    private final TechCentreRepository techCentreRepository;
    private final OwnerTeamRepository ownerTeamRepository;
    private final ApiService apiService;
    private final EntityManager entityManager;

    public UpdateServiceImpl(
            PositionRepository positionRepository,
            DirectionRepository directionRepository, TechCentreRepository techCentreRepository,
            OwnerTeamRepository ownerTeamRepository, ApiService apiService,
            EntityManager entityManager) {
        this.positionRepository = positionRepository;
        this.directionRepository = directionRepository;
        this.techCentreRepository = techCentreRepository;
        this.ownerTeamRepository = ownerTeamRepository;
        this.apiService = apiService;
        this.entityManager = entityManager;
    }

    @Override
    public void syncPositions() {
        PositionDtoResponse[] responseArray = apiService.fetchPositions();
        syncPositionsFromArray(responseArray);
    }

    @Override
    public void syncPositionsFromArray(PositionDtoResponse[] responseArray) {
        if (responseArray == null || responseArray.length == 0) {
            return;
        }

        List<PositionDtoResponse> dtos = Arrays.asList(responseArray);
        int total = dtos.size();

        for (int i = 0; i < total; i++) {
            syncSinglePosition(dtos.get(i));

            if ((i + 1) % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
                log.info("Импорт позиций: {}/{} обработано", i + 1, total);
            }
        }

        entityManager.flush();
        entityManager.clear();
        log.info("Импорт позиций завершён: {}/{}", total, total);
    }

    private void syncSinglePosition(PositionDtoResponse dto) {
        Position position = positionRepository.findByPositionUUID(dto.getPositionUUID())
                .orElseGet(() -> new Position());

        boolean hasChanges = false;
        boolean isNew = position.getId() == null;

        // UUID всегда ставим для новых
        if (isNew) {
            position.setPositionUUID(dto.getPositionUUID());
            hasChanges = true;
        }

        // Direction - получаем/создаем по title
        Direction direction = findOrCreateDirection(dto.getPositionTechnapr());
        if (!Objects.equals(position.getPositionTechnapr(), direction)) {
            position.setPositionTechnapr(direction);
            hasChanges = true;
        }

        // TechCentre - получаем/создаем по title
        TechCentre techCentre = findOrCreateTechCentre(dto.getPositionTechcentre());
        if (!Objects.equals(position.getPositionTechcentre(), techCentre)) {
            position.setPositionTechcentre(techCentre);
            hasChanges = true;
        }

        // OwnerTeam - берем первый из массива
        OwnerTeam ownerTeam = findOrCreateOwnerTeam(dto.getOwnerTeam_te());
        if (!Objects.equals(position.getOwnerTeam(), ownerTeam)) {
            position.setOwnerTeam(ownerTeam);
            hasChanges = true;
        }

        // Простые поля
        if (!Objects.equals(position.getPositionGradlong(), dto.getPositionGradlong())) {
            position.setPositionGradlong(dto.getPositionGradlong());
            hasChanges = true;
        }

        if (!Objects.equals(position.getPositionGradlat(), dto.getPositionGradlat())) {
            position.setPositionGradlat(dto.getPositionGradlat());
            hasChanges = true;
        }

        if (!Objects.equals(position.getPositionNameforbs(), dto.getPositionNameforbs())) {
            position.setPositionNameforbs(dto.getPositionNameforbs());
            hasChanges = true;
        }

        if (!Objects.equals(position.getAddressStr(), dto.getAddressStr())) {
            position.setAddressStr(dto.getAddressStr());
            hasChanges = true;
        }

        boolean bsChanged = syncBaseStations(position, dto.getBaseStations());

        if (hasChanges || bsChanged) {
            position.setLastSyncDate(LocalDateTime.now());
            positionRepository.save(position);
        }
    }

    // методы для find-or-create справочников

    private Direction findOrCreateDirection(String title) {
        if (title == null || title.trim().isEmpty()) {
            return null;
        }

        return directionRepository.findByTitle(title)
                .orElseGet(() -> {
                    Direction newDirection = Direction.builder()
                            .title(title)
                            .build();
                    return directionRepository.save(newDirection);
                });
    }

    private TechCentre findOrCreateTechCentre(String title) {
        if (title == null || title.trim().isEmpty()) {
            return null;
        }

        return techCentreRepository.findByTitle(title)
                .orElseGet(() -> {
                    TechCentre newTechCentre = TechCentre.builder()
                            .title(title)
                            .build();
                    return techCentreRepository.save(newTechCentre);
                });
    }

    private OwnerTeam findOrCreateOwnerTeam(List<OwnerTeamDtoResponse> ownerTeamList) {

        if (ownerTeamList == null || ownerTeamList.isEmpty()) {
            return null;
        }

        OwnerTeamDtoResponse dto = ownerTeamList.get(0);

        if (dto.getId() == null) {
            return null;
        }

        String externalId = dto.getId();

        return ownerTeamRepository.findByExternalId(externalId)
                .orElseGet(() -> {
                    OwnerTeam newTeam = OwnerTeam.builder()
                            .externalId(externalId)
                            .title(dto.getTitle())
                            .build();
                    return ownerTeamRepository.save(newTeam);
                });
    }


    private boolean syncBaseStations(Position position, List<BaseStationDtoResponse> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return false;
        }

        boolean hasChanges = false;

        Set<String> incomingUUIDs = dtos.stream()
                .map(BaseStationDtoResponse::getBaseStationUUID)
                .collect(Collectors.toSet());

        List<BaseStation> toRemove = position.getBaseStations().stream()
                .filter(bs -> !incomingUUIDs.contains(bs.getBaseStationUUID()))
                .toList();

        if (!toRemove.isEmpty()) {
            for (BaseStation station : toRemove) {
                position.removeBaseStation(station);
            }
            hasChanges = true;
        }

        for (BaseStationDtoResponse dto : dtos) {
            boolean stationChanged = syncSingleBaseStation(position, dto);
            hasChanges = hasChanges || stationChanged;
        }

        return hasChanges;
    }

    private boolean syncSingleBaseStation(Position position, BaseStationDtoResponse dto) {
        BaseStation existingStation = position.getBaseStations().stream()
                .filter(bs -> bs.getBaseStationUUID().equals(dto.getBaseStationUUID()))
                .findFirst()
                .orElse(null);

        if (existingStation == null) {
            BaseStation newStation = new BaseStation();
            newStation.setBaseStationUUID(dto.getBaseStationUUID());
            newStation.setBaseStationName(dto.getBaseStationAdName());
            newStation.setBaseStationDispTitle(dto.getBaseStationDispTitle());
            newStation.setBaseStationAddressDoc(dto.getBaseStationAddressDoc());

            position.addBaseStation(newStation);
            return true;
        }

        boolean hasChanges = false;

        if (!Objects.equals(existingStation.getBaseStationName(), dto.getBaseStationAdName())) {
            existingStation.setBaseStationName(dto.getBaseStationAdName());
            hasChanges = true;
        }

        if (!Objects.equals(existingStation.getBaseStationDispTitle(), dto.getBaseStationDispTitle())) {
            existingStation.setBaseStationDispTitle(dto.getBaseStationDispTitle());
            hasChanges = true;
        }

        if (!Objects.equals(existingStation.getBaseStationAddressDoc(), dto.getBaseStationAddressDoc())) {
            existingStation.setBaseStationAddressDoc(dto.getBaseStationAddressDoc());
            hasChanges = true;
        }

        return hasChanges;
    }
}
