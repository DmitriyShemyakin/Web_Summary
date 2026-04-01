// com.example.web_summaryy.model.IncidentCategory

package com.example.web_summaryy.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Справочник "Авария" (категория/полное название)
 * 
 * Примеры:
 * - "Аварийное отключение сети"
 * - "Блокировка БС"
 * - "Нет управления IP ТМ по OSS"
 * - "Непрерывность выпрямителя"
 */
@Entity
@Table(name = "incident_categories", schema = "web_summary_motiv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название категории аварии
     * Пример: "Аварийное отключение сети"
     */
    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    /**
     * Краткое описание категории (опционально)
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Признак активности справочника
     */
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}




