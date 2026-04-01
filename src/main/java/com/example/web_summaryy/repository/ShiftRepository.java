// com.example.web_summaryy.repository.ShiftRepository

package com.example.web_summaryy.repository;

import com.example.web_summaryy.model.Shift;
import com.example.web_summaryy.model.ShiftStatus;
import com.example.web_summaryy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository для работы со сменами
 */
@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    /**
     * Найти все смены по статусу
     */
    List<Shift> findByStatus(ShiftStatus status);

    /**
     * Найти активную смену (если есть)
     */
    Optional<Shift> findFirstByStatusOrderByStartedAtDesc(ShiftStatus status);

    /**
     * Найти смены за период
     */
    List<Shift> findByStartedAtBetweenOrderByStartedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Найти последнюю закрытую смену
     */
    @Query("SELECT s FROM Shift s WHERE s.status = 'CLOSED' ORDER BY s.endedAt DESC LIMIT 1")
    Optional<Shift> findLastClosedShift();

    /**
     * Найти смены по пользователю
     */
    List<Shift> findByDutyUser(User user);

    /**
     * Найти смены по ID пользователя
     */
    List<Shift> findByDutyUserId(Long userId);

    /**
     * Найти активную смену пользователя
     */
    Optional<Shift> findByDutyUserAndStatus(User user, ShiftStatus status);

    /**
     * Найти все смены пользователя с пагинацией
     */
    Page<Shift> findByDutyUserOrderByStartedAtDesc(User user, Pageable pageable);

    /**
     * Найти все смены с пагинацией
     */
    Page<Shift> findAllByOrderByStartedAtDesc(Pageable pageable);
}

