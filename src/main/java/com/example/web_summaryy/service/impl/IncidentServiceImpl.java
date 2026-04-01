package com.example.web_summaryy.service.impl;

import com.example.web_summaryy.dto.incident.*;
import com.example.web_summaryy.config.mapper.IncidentMapper;
import com.example.web_summaryy.model.*;
import com.example.web_summaryy.repository.IncidentRepository;
import com.example.web_summaryy.repository.ShiftRepository;
import com.example.web_summaryy.service.IncidentService;
import com.example.web_summaryy.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class IncidentServiceImpl implements IncidentService {

    /** Длительность в виде ЧЧ:ММ (минуты 00–59, часы 0–999). */
    private static final Pattern DURATION_HHMM = Pattern.compile("^\\d{1,3}:[0-5]\\d$");

    private final IncidentRepository incidentRepository;
    private final IncidentMapper incidentMapper;

    private final ShiftRepository shiftRepository;
    private final UserService userService;

    public IncidentServiceImpl(
            IncidentRepository incidentRepository,
            IncidentMapper incidentMapper,
            UserService userService,
            ShiftRepository shiftRepository) {
        this.incidentRepository = incidentRepository;
        this.incidentMapper = incidentMapper;
        this.userService = userService;
        this.shiftRepository = shiftRepository;
    }

    @Override
    public IncidentDtoResponse createIncident(CreateIncidentRequest request, User currentUser) {
        validateCreateRequest(request);

        shiftRepository.findByDutyUserAndStatus(currentUser, ShiftStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException(
                        "Начните смену перед созданием аварии"));

        Incident incident = incidentMapper.toEntity(request, currentUser);

        LocalDateTime now = LocalDateTime.now();
        if (incident.getCreatedAt() == null) {
            incident.setCreatedAt(now);
        }
        if (incident.getUpdatedAt() == null) {
            incident.setUpdatedAt(now);
        }

        incident = incidentRepository.save(incident);
        return incidentMapper.toDto(incident);
    }

    @Override
    public IncidentDtoResponse updateIncident(Long id, UpdateIncidentRequest request, User currentUser) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Авария не найдена: " + id));

        validateUpdateRequest(request);
        incidentMapper.updateEntity(incident, request, currentUser);
        incident = incidentRepository.save(incident);
        return incidentMapper.toDto(incident);
    }

    @Override
    public IncidentDtoResponse closeIncident(Long id, CloseIncidentRequest request, User currentUser) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Авария не найдена: " + id));

        if (IncidentStatus.CLOSED.equals(incident.getStatus())) {
            throw new RuntimeException("Нельзя закрыть уже закрытую аварию");
        }

        if (request.getEndedAt() == null) {
            throw new IllegalArgumentException("Укажите дату и время окончания аварии");
        }

        if (incident.getStartedAt() != null && request.getEndedAt().isBefore(incident.getStartedAt())) {
            throw new RuntimeException("Дата окончания не может быть раньше даты начала");
        }

        if (request.getConnectionDowntime() == null || request.getConnectionDowntime().isBlank()) {
            throw new IllegalArgumentException("Укажите время перерыва связи (формат ЧЧ:ММ)");
        }
        validateOptionalDurationHHMM(request.getConnectionDowntime(), "Время перерыва связи");
        validateOptionalDurationHHMM(request.getAkbDuration(), "АКБ");
        validateOptionalDurationHHMM(request.getGuDuration(), "ГУ");
        validateOptionalDurationHHMM(request.getBsTotalDowntime(), "Общее время простоя БС");

        Shift activeShift = shiftRepository.findByDutyUserAndStatus(currentUser, ShiftStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("У вас нет активной смены"));

        incidentMapper.closeEntity(incident, request, currentUser, activeShift);
        incident = incidentRepository.save(incident);
        return incidentMapper.toDto(incident);
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentDtoResponse getIncidentById(Long id, User currentUser) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Авария не найдена: " + id));
        return incidentMapper.toDto(incident);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidentDtoResponse> getOpenIncidents(User currentUser) {
        return shiftRepository.findByDutyUserAndStatus(currentUser, ShiftStatus.ACTIVE)
                .map(activeShift -> mapToDtoSorted(loadMainDashboardDuringShift(activeShift, currentUser)))
                .orElseGet(() -> mapToDtoSorted(incidentRepository.findByStatusAndCreatedByOrderByStartedAtDesc(
                        IncidentStatus.OPEN, currentUser)));
    }

    /**
     * Главная при активной смене: все открытые аварии с той же видимостью, что на экране смены,
     * плюс закрытые этим пользователем в интервале смены.
     */
    private List<Incident> loadMainDashboardDuringShift(Shift activeShift, User currentUser) {
        LocalDateTime shiftStart = activeShift.getStartedAt();
        LocalDateTime shiftEnd = activeShift.getEndedAt() != null
                ? activeShift.getEndedAt()
                : LocalDateTime.now();

        Set<Long> seen = new HashSet<>();
        List<Incident> merged = new ArrayList<>();
        for (Incident i : incidentRepository.findByStatusOrderByStartedAtDesc(IncidentStatus.OPEN)) {
            if (!seen.add(i.getId())) {
                continue;
            }
            if (isIncidentVisibleToViewer(i, currentUser, activeShift)) {
                merged.add(i);
            }
        }
        for (Incident i : incidentRepository.findClosedByUserBetween(currentUser, shiftStart, shiftEnd)) {
            if (seen.add(i.getId())) {
                merged.add(i);
            }
        }
        merged.sort(Comparator.comparing(Incident::getStartedAt).reversed());
        return merged;
    }

    private List<IncidentDtoResponse> mapToDtoSorted(List<Incident> incidents) {
        return incidents.stream().map(incidentMapper::toDto).collect(Collectors.toList());
    }

    /** Видимость как на списке аварий смены (роли, направления, автор, привязка к смене). */
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


    @Override
    @Transactional(readOnly = true)
    public List<IncidentDtoResponse> getClosedIncidents(LocalDateTime from, LocalDateTime to, User currentUser) {

        Set<Direction> directions = getDirectionsByRolesAndUser(currentUser);
        List<Incident> closedIncidents = incidentRepository.findClosedIncidentsBetween(from, to, directions);

        return closedIncidents.stream()
                .map(incidentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteIncident(Long id, User currentUser) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Авария не найдена: " + id));
        incidentRepository.delete(incident);
    }

    private void validateCreateRequest(CreateIncidentRequest request) {
        if (request.getStartedAt() == null) {
            throw new RuntimeException("Не указана дата начала аварии");
        }

        if (request.getPositionIds() == null || request.getPositionIds().isEmpty()) {
            throw new RuntimeException("Не выбраны позиции");
        }

        if (request.getIncidentTypeId() == null) {
            throw new RuntimeException("Не выбран тип аварии");
        }

        if (request.getIncidentCategoryId() == null) {
            throw new RuntimeException("Не выбрана категория аварии");
        }

        if (request.getNetworkTypeId() == null) {
            throw new RuntimeException("Не выбран тип сети");
        }

        if (request.getPositionLevel() == null) {
            throw new RuntimeException("Не указана категория (уровень позиции)");
        }
        if (request.getPositionLevel() < 0 || request.getPositionLevel() > 3) {
            throw new RuntimeException("Категория (уровень позиции) должна быть от 0 до 3");
        }
        validateOptionalDurationHHMM(request.getAkbDuration(), "АКБ");
        validateOptionalDurationHHMM(request.getGuDuration(), "ГУ");
        validateOptionalDurationHHMM(request.getConnectionDowntime(), "Время перерыва связи");
        validateOptionalDurationHHMM(request.getBsTotalDowntime(), "Общее время простоя БС");
    }

    private void validateUpdateRequest(UpdateIncidentRequest request) {
        if (request.getPositionLevel() == null) {
            throw new RuntimeException("Не указана категория (уровень позиции)");
        }
        if (request.getPositionLevel() < 0 || request.getPositionLevel() > 3) {
            throw new RuntimeException("Категория (уровень позиции) должна быть от 0 до 3");
        }
        if (request.getNetworkTypeId() == null) {
            throw new RuntimeException("Не выбран тип сети");
        }
        validateOptionalDurationHHMM(request.getAkbDuration(), "АКБ");
        validateOptionalDurationHHMM(request.getGuDuration(), "ГУ");
        validateOptionalDurationHHMM(request.getConnectionDowntime(), "Время перерыва связи");
        validateOptionalDurationHHMM(request.getBsTotalDowntime(), "Общее время простоя БС");
    }

    private void validateOptionalDurationHHMM(String value, String fieldLabel) {
        if (value == null || value.isBlank()) {
            return;
        }
        String t = value.trim();
        if (!DURATION_HHMM.matcher(t).matches()) {
            throw new IllegalArgumentException(fieldLabel + ": укажите длительность в формате ЧЧ:ММ (например 02:30)");
        }
    }

    private Set<Direction> getDirectionsByRolesAndUser(User currentUser) {
        Set<Direction> directions = new HashSet<>(currentUser.getDirections());
        if (currentUser.getObjRole() != null) {
            for (var role : currentUser.getObjRole()) {
                if (role.getDirections() != null) {
                    directions.addAll(role.getDirections());
                }
            }
        }
        return directions;
    }
}

