package com.example.web_summaryy.repository;

import com.example.web_summaryy.model.TechCentre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechCentreRepository extends JpaRepository<TechCentre, Long> {
    Optional<TechCentre> findByTitle(String title);
}
