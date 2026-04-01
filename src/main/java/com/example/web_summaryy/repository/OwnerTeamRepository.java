package com.example.web_summaryy.repository;

import com.example.web_summaryy.model.OwnerTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerTeamRepository extends JpaRepository<OwnerTeam, Long> {
    Optional<OwnerTeam> findByExternalId(String externalId);
}
