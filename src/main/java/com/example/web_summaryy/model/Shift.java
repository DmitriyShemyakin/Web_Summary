// com.example.web_summaryy.model.Shift

package com.example.web_summaryy.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Смена дежурного
 *
 * Используется для группировки аварий по сменам
 * и генерации Excel-отчетов за смену
 *
 * Опциональная сущность для MVP
 */
@Entity
@Table(name = "shifts", schema = "web_summary_motiv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дежурный, ведущий смену
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "duty_user_id")
    private User dutyUser;

    /**
     * Дата и время начала смены
     */
    @CreatedDate
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    /**
     * Дата и время окончания смены
     */
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    /**
     * Путь к сгенерированному Excel-файлу
     */
    @Column(name = "excel_file_path")
    private String excelFilePath;

    /**
     * Статус смены
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ShiftStatus status = ShiftStatus.ACTIVE;

    /**
     * Закрыть смену
     */
    public void close() {
        this.status = ShiftStatus.CLOSED;
        this.endedAt = LocalDateTime.now();
    }
}


