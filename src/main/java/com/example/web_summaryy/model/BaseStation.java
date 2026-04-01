// com.example.web_summaryy.model.BaseStation

package com.example.web_summaryy.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "base_stations", schema = "web_summary_motiv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class BaseStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base_station_uuid", nullable = false, unique = true)
    private String baseStationUUID;

    @Column(name = "base_station_name")
    private String baseStationName;

    @Column(name = "disp_title")
    private String baseStationDispTitle;

    @Column(name = "address_doc", columnDefinition = "TEXT")
    private String baseStationAddressDoc;

    @Builder.Default
    @Column(name = "is_skip")
    private Boolean isSkip = false;

    @CreatedDate
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;
}
