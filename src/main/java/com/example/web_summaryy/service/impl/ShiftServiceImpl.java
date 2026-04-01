package com.example.web_summaryy.service.impl;

import com.example.web_summaryy.dto.incident.IncidentDtoResponse;
import com.example.web_summaryy.dto.shift.EndShiftRequest;
import com.example.web_summaryy.dto.shift.EndShiftResponse;
import com.example.web_summaryy.dto.shift.ShiftDtoResponse;
import com.example.web_summaryy.dto.shift.StartShiftRequest;
import com.example.web_summaryy.config.mapper.IncidentMapper;
import com.example.web_summaryy.config.mapper.ShiftMapper;
import com.example.web_summaryy.model.*;
import com.example.web_summaryy.repository.IncidentRepository;
import com.example.web_summaryy.repository.ShiftRepository;
import com.example.web_summaryy.service.ShiftService;
import com.example.web_summaryy.service.UserService;
import com.example.web_summaryy.service.export.ShiftIncidentXlsxExporter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final IncidentRepository incidentRepository;
    private final ShiftMapper shiftMapper;
    private final IncidentMapper incidentMapper;
    private final UserService userService;
    private final ShiftIncidentXlsxExporter shiftIncidentXlsxExporter;

    @Override
    @Transactional
    public ShiftDtoResponse startShift(StartShiftRequest request, User currentUser) {

        log.info("Starting shift for user: {}", currentUser.getUsername());

        // Проверяем, нет ли уже активной смены
        shiftRepository.findByDutyUserAndStatus(currentUser, ShiftStatus.ACTIVE)
                .ifPresent(activeShift -> {
                    throw new IllegalStateException("У вас уже есть активная смена. Завершите её перед началом новой.");
                });

        Shift shift = Shift.builder()
                .dutyUser(currentUser)
                .status(ShiftStatus.ACTIVE)
                .startedAt(LocalDateTime.now())
                .build();

        Shift savedShift = shiftRepository.save(shift);
        log.info("Shift started with ID: {}", savedShift.getId());

        return shiftMapper.toDto(savedShift);
    }

    @Override
    @Transactional
    public EndShiftResponse endShift(EndShiftRequest request, User currentUser) {
        log.info("Ending shift for user: {}", currentUser.getUsername());

        // Находим активную смену
        Shift activeShift = shiftRepository.findByDutyUserAndStatus(currentUser, ShiftStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("У вас нет активной смены. " +
                        "Необходимо начать смену перед закрытием аварии"));

        List<Incident> incidentsInWindow = loadIncidentsForShift(activeShift, currentUser);
        long closedCount = incidentsInWindow.stream()
                .filter(i -> i.getStatus() == IncidentStatus.CLOSED)
                .count();
        long openCount = incidentsInWindow.stream()
                .filter(i -> i.getStatus() == IncidentStatus.OPEN)
                .count();

        // Закрываем смену
        activeShift.close();

        Shift closedShift = shiftRepository.save(activeShift);

        String downloadUrl = null;
        if (Boolean.TRUE.equals(request.getGenerateReport())) {
            downloadUrl = "/api/shifts/" + closedShift.getId() + "/export.xlsx";
            log.info("Shift {} closed; XLSX export available at {}", closedShift.getId(), downloadUrl);
        }
        log.info("Shift ended with ID: {}. Closed incidents: {}, Open incidents: {}",
                closedShift.getId(), closedCount, openCount);

        return EndShiftResponse.builder()
                .shift(shiftMapper.toDto(closedShift))
                .downloadUrl(downloadUrl)
                .closedIncidentsCount((int) closedCount)
                .openIncidentsCount((int) openCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftDtoResponse getCurrentShift(User currentUser) {
        return shiftRepository.findByDutyUserAndStatus(currentUser, ShiftStatus.ACTIVE)
                .map(shiftMapper::toDto)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftDtoResponse getShiftById(Long shiftId, User currentUser) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new EntityNotFoundException("Смена не найдена"));
        return shiftMapper.toDto(shift);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShiftDtoResponse> getShiftHistory(User currentUser, Pageable pageable) {
        return shiftRepository.findByDutyUserOrderByStartedAtDesc(currentUser, pageable)
                .map(shiftMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShiftDtoResponse> getAllShifts(Pageable pageable) {
        return shiftRepository.findAllByOrderByStartedAtDesc(pageable)
                .map(shiftMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidentDtoResponse> getShiftIncidents(Long shiftId, User currentUser) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new EntityNotFoundException("Смена не найдена"));

        List<Incident> incidents = loadIncidentsForShift(shift, currentUser);

        return incidents.stream()
                .map(incidentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Аварии смены: пересечение интервала «жизни» аварии с [начало, конец] смены + записи с {@code shift_id} = эта смена.
     * Учитываются аварии, открытые в смену и закрытые уже после её окончания.
     */
    private List<Incident> loadIncidentsForShift(Shift shift, User viewer) {
        LocalDateTime start = shift.getStartedAt();
        LocalDateTime end = shift.getEndedAt() != null ? shift.getEndedAt() : LocalDateTime.now();

        Set<Long> seen = new HashSet<>();
        List<Incident> merged = new ArrayList<>();
        for (Incident i : incidentRepository.findIncidentsIntersectingShiftPeriod(start, end)) {
            if (seen.add(i.getId())) {
                merged.add(i);
            }
        }
        for (Incident i : incidentRepository.findByShift_IdOrderByStartedAtDesc(shift.getId())) {
            if (seen.add(i.getId())) {
                merged.add(i);
            }
        }


        return merged
                .stream()
                .filter(i -> isIncidentVisibleToViewer(i, viewer, shift))
                .sorted(Comparator.comparing(Incident::getStartedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportShiftIncidentsAsXlsx(Long shiftId, User currentUser) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new EntityNotFoundException("Смена не найдена"));
        List<Incident> incidents = loadIncidentsForShift(shift, currentUser);
        try {
            return shiftIncidentXlsxExporter.export(shift, incidents);
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось сформировать XLSX", e);
        }
    }

    private boolean isIncidentVisibleToViewer(Incident incident, User viewer, Shift shift) {
        if (userService.seesAllIncidentsByObjRoles(viewer)) {
            return true;
        }
        if (incident.getCreatedBy() != null && incident.getCreatedBy().getId().equals(viewer.getId())) {
            return true;
        }
        Shift incShift = incident.getShift();
        if (incShift != null && incShift.getId().equals(shift.getId())) {
            return true;
        }
        Set<Long> viewerDirIds = directionIdsOf(getDirectionsByRolesAndUser(viewer));
        if (viewerDirIds.isEmpty()) {
            return false;
        }
        return incident.getPositions().stream()
                .map(Position::getPositionTechnapr)
                .filter(Objects::nonNull)
                .map(Direction::getId)
                .filter(Objects::nonNull)
                .anyMatch(viewerDirIds::contains);
    }

    private Set<Long> directionIdsOf(Set<Direction> directions) {
        return directions.stream()
                .map(Direction::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<Direction> getDirectionsByRolesAndUser(User currentUser) {
        Set<Direction> directions = new HashSet<>(currentUser.getDirections());
        if (currentUser.getObjRole() != null) {
            for (Role role : currentUser.getObjRole()) {
                if (role.getDirections() != null) {
                    directions.addAll(role.getDirections());
                }
            }
        }
        return directions;
    }

}

