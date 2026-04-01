package com.example.web_summaryy.repository;

import com.example.web_summaryy.model.BaseStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaseStationRepository extends JpaRepository<BaseStation, Long> {

    Optional<BaseStation> findByBaseStationUUID(String baseStationUUID);

    @Query("SELECT bs FROM BaseStation bs JOIN bs.position p WHERE p.positionTechcentre = :techcentre")
    List<BaseStation> findByPositionTechcentre(@Param("techcentre") String techcentre);
}
