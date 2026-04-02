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

    @Column(name = "incident_number")
    private String incidentNumber;

    @Column(name = "incident_number_url")
    private String incidentNumberUrl;

    @Column(name = "equipment_type")
    private String equipmentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_type_id")
    private IncidentType incidentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_category_id")
    private IncidentCategory incidentCategory;

    @Column(name = "position_level")
    private Integer positionLevel;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "incident_positions",
        schema = "web_summary_motiv",
        joinColumns = @JoinColumn(name = "incident_id"),
        inverseJoinColumns = @JoinColumn(name = "position_id")
    )
    private Set<Position> positions = new HashSet<>();

    @Column(name = "base_stations_count")
    private String baseStationsCount;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "akb_duration")
    private String akbDuration;

    @Column(name = "gu_duration")
    private String guDuration;

    @Column(name = "connection_downtime")
    private String connectionDowntime;

    @Column(name = "bs_total_downtime")
    private String bsTotalDowntime;

    @Column(name = "notification_text", columnDefinition = "TEXT")
    private String notificationText;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private IncidentStatus status = IncidentStatus.OPEN;

    @Builder.Default
    @OneToMany(mappedBy = "incident", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Direction> directions = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by_user_id")
    private User closedBy;

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


