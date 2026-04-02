// com.example.web_summaryy.repository.IncidentCategoryRepository

package com.example.web_summaryy.repository;

import com.example.web_summaryy.model.IncidentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncidentCategoryRepository extends JpaRepository<IncidentCategory, Long> {

    Optional<IncidentCategory> findByCategoryName(String categoryName);

    List<IncidentCategory> findByIsActiveTrue();

    boolean existsByCategoryName(String categoryName);
}




