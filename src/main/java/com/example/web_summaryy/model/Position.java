// com.example.web_summaryy.model.Position

package com.example.web_summaryy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "positions", schema = "web_summary_motiv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Position {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "position_uuid", nullable = false, unique = true)
    private String positionUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direction_id")
    private Direction positionTechnapr;

    @Column(name = "position_gradlong")
    private Double positionGradlong;

    @Column(name = "position_gradlat")
    private Double positionGradlat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_centre_id")
    private TechCentre positionTechcentre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_team_id")
    private OwnerTeam ownerTeam;

    @Column(name = "position_nameforbs")
    private String positionNameforbs;

    @Column(name = "address")
    private String addressStr;

    @CreatedDate
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @LastModifiedDate
    @Column(name = "last_sync_date")
    private LocalDateTime lastSyncDate;

    @Builder.Default
    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore // избегаем циклической сериализации, на всякий случай..
    private List<BaseStation> baseStations = new ArrayList<>();

    public void addBaseStation(BaseStation station) {
        station.setPosition(this);
        this.baseStations.add(station);
    }

    public void removeBaseStation(BaseStation station) {
        this.baseStations.remove(station);
        station.setPosition(null);
    }
}
