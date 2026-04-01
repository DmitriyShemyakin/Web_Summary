// com.example.web_summaryy.model.Incident

package com.example.web_summaryy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сущность "Авария" (Incident)
 *
 * Представляет собой запись об аварии на сети.
 * Одна авария может быть связана с несколькими позициями (many-to-many).
 * Статусы: OPEN (открыта), CLOSED (закрыта)
 */
@Entity
@Table(name = "incidents", schema = "web_summary_motiv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Номер NTT/PW
     * Пример: "2346-59-11"
     */
    @Column(name = "incident_number")
    private String incidentNumber;

    /**
     * URL/ссылка на заявку во внешней системе
     * Можно сделать гиперссылкой в UI
     */
    @Column(name = "incident_number_url")
    private String incidentNumberUrl;

    /**
     * Тип оборудования (свободный текст, вводится вручную)
     * Пример: "Выпрямитель EMERSON", "MOTOREVA"
     */
    @Column(name = "equipment_type")
    private String equipmentType;

    /**
     * Справочник "Тип" (ТМ, ЭЛ, РРЛ, EMERSON, FA-13G и т.д.)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_type_id")
    private IncidentType incidentType;

    /**
     * Справочник "Авария" (полное название аварии)
     * Пример: "Аварийное отключение сети", "Блокировка БС"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_category_id")
    private IncidentCategory incidentCategory;

    /**
     * Уровень позиции (0–3), задаётся вручную. Не путать со справочником {@link #incidentCategory}.
     */
    @Column(name = "position_level")
    private Integer positionLevel;

    /**
     * Связь many-to-many с позициями
     * Одна авария может затрагивать несколько позиций
     */
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "incident_positions",
        schema = "web_summary_motiv",
        joinColumns = @JoinColumn(name = "incident_id"),
        inverseJoinColumns = @JoinColumn(name = "position_id")
    )
    private Set<Position> positions = new HashSet<>();

    /**
     * Количество БС (базовых станций)
     * По умолчанию = количество выбранных позиций,
     * но может редактироваться вручную
     */
    @Column(name = "base_stations_count")
    private String baseStationsCount;

    /**
     * Дата и время начала аварии
     */
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    /**
     * Дата и время окончания аварии (null если авария открыта)
     */
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    /**
     * Время работы от АКБ (аккумулятора)
     * Формат: свободный текст, вводится вручную
     * Пример: "00:56", "1:30:00"
     */
    @Column(name = "akb_duration")
    private String akbDuration;

    /**
     * Время работы от ГУ (генераторной установки)
     * Формат: свободный текст, вводится вручную
     */
    @Column(name = "gu_duration")
    private String guDuration;

    /**
     * Время перерыва связи
     * Вводится вручную
     */
    @Column(name = "connection_downtime")
    private String connectionDowntime;

    /**
     * Общее время простоя БС
     * Вводится вручную
     */
    @Column(name = "bs_total_downtime")
    private String bsTotalDowntime;

    /**
     * Уведомление об аварии (текст)
     * Пример: "Контакт-центру А.Б."
     */
    @Column(name = "notification_text", columnDefinition = "TEXT")
    private String notificationText;

    /**
     * Описание / Примечание
     * Дополнительная информация об аварии
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Статус аварии: OPEN (открыта), CLOSED (закрыта)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private IncidentStatus status = IncidentStatus.OPEN;

    @Builder.Default
    @OneToMany(mappedBy = "incident", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Direction> directions = new ArrayList<>();

    /**
     * Дата создания записи (автоматически)
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата последнего изменения (автоматически)
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Дата закрытия аварии (заполняется при закрытии)
     */
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    /**
     * Смена, в рамках которой создана авария (активная смена создателя на момент создания).
     * Nullable для записей до введения привязки и для миграций.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    /**
     * Пользователь, создавший аварию
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    /**
     * Пользователь, закрывший аварию
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by_user_id")
    private User closedBy;

    /**
     * Пользователь, последний изменивший аварию
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "network_type_id")
    private NetworkType networkType;

    // --- Вспомогательные методы ---

    /**
     * Добавить позицию к аварии
     */
    public void addPosition(Position position) {
        this.positions.add(position);
    }

    /**
     * Удалить позицию из аварии
     */
    public void removePosition(Position position) {
        this.positions.remove(position);
    }

    /**
     * Закрыть аварию
     */
    public void close() {
        this.status = IncidentStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
    }

    /**
     * Вычислить длительность аварии (если есть дата окончания)
     * Возвращает строку вида "02:30:00" или null
     */
    public String calculateDuration() {
        if (startedAt != null && endedAt != null) { // todo: убрать endedAt длительность дальше рассчитываться без endedAt
            java.time.Duration duration = java.time.Duration.between(startedAt, endedAt);
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            long seconds = duration.toSecondsPart();
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return null;
    }
}


