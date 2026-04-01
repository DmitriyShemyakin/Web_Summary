// com.example.web_summaryy.repository.IncidentRepository

package com.example.web_summaryy.repository;

import com.example.web_summaryy.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Repository для работы с авариями
 */
@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    /**
     * Найти все аварии по статусу
     */
    List<Incident> findByStatus(IncidentStatus status);

    /**
     * Найти все открытые аварии
     */
    List<Incident> findByStatusOrderByStartedAtDesc(IncidentStatus status);

    /**
     * Открытые аварии, созданные пользователем (главная без активной смены).
     */
    List<Incident> findByStatusAndCreatedByOrderByStartedAtDesc(IncidentStatus status, User createdBy);

    /**
     * Закрытые пользователем в интервале [start, end] (главная при активной смене).
     */
    @Query("""
            SELECT i FROM Incident i
            WHERE i.status = com.example.web_summaryy.model.IncidentStatus.CLOSED
              AND i.closedBy = :closedBy
              AND i.closedAt >= :start AND i.closedAt <= :end
            ORDER BY i.closedAt DESC
            """)
    List<Incident> findClosedByUserBetween(
            @Param("closedBy") User closedBy,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("""
        SELECT DISTINCT i
        FROM Incident i
        JOIN i.positions p
        JOIN p.positionTechnapr d
        WHERE d IN :directions
          AND i.startedAt < :end
          AND (i.endedAt IS NULL OR i.endedAt > :start)""")
    List<Incident> findAllForShiftSummary(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("directions") Set<Direction> directions);




    /**
     * Найти аварии по номеру заявки
     */
    List<Incident> findByIncidentNumberContaining(String incidentNumber);

    /**
     * Найти аварии за период времени
     */
    List<Incident> findByStartedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Найти аварии по типу
     */
    List<Incident> findByIncidentType_Id(Long incidentTypeId);

    /**
     * Найти аварии по категории
     */
    List<Incident> findByIncidentCategory_Id(Long incidentCategoryId);

    /**
     * Найти аварии по позиции (через many-to-many)
     */
    @Query("SELECT i FROM Incident i JOIN i.positions p WHERE p.id = :positionId")
    List<Incident> findByPositionId();

    /**
     * Найти закрытые аварии за период c учетом прав
     */
    @Query("SELECT i FROM Incident i " +
            "JOIN i.positions p " +
            "WHERE i.status = 'CLOSED' " +
            "AND i.closedAt BETWEEN :startDate AND :endDate AND p.positionTechnapr IN :directions " +
            "ORDER BY i.closedAt DESC")
    List<Incident> findClosedIncidentsBetween(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate,
                                              @Param("directions") Set<Direction> directions);

    /**
     * Подсчитать количество открытых аварий
     */
    long countByStatus(IncidentStatus status);

    /**
     * Найти аварии по создателю и периоду создания
     */
    List<Incident> findByCreatedByAndCreatedAtBetween(User createdBy, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Все аварии, привязанные к смене (после закрытия аварии в этой смене)
     */
    List<Incident> findByShift_IdOrderByStartedAtDesc(Long shiftId);

    long countByShift_IdAndStatus(Long shiftId, IncidentStatus status);

    /**
     * Аварии, которые пересекались со сменой по времени: начались не позже конца смены
     * и не были закрыты раньше начала смены (в т.ч. закрыты после окончания смены, но были открыты в неё).
     * Видимость накладывается в сервисе.
     */
    @Query("""
            SELECT i FROM Incident i
            WHERE i.startedAt <= :end
              AND (i.closedAt IS NULL OR i.closedAt >= :start)
            ORDER BY i.startedAt DESC
            """)
    List<Incident> findIncidentsIntersectingShiftPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}




