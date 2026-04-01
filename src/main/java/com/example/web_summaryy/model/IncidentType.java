// com.example.web_summaryy.model.IncidentType

package com.example.web_summaryy.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "incident_types", schema = "web_summary_motiv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Код типа (короткое обозначение)
     * Пример: "ТМ", "ЭЛ", "РРЛ", "FA-13G"
     */
    @Column(name = "type_code", nullable = false, unique = true)
    private String typeCode;

    /**
     * Признак активности справочника
     */
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}




