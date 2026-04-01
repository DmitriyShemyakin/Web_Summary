// com.example.web_summaryy.repository.IncidentCategoryRepository

package com.example.web_summaryy.repository;

import com.example.web_summaryy.model.IncidentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository для работы со справочником категорий аварий
 */
@Repository
public interface IncidentCategoryRepository extends JpaRepository<IncidentCategory, Long> {

    /**
     * Найти категорию по названию
     */
    Optional<IncidentCategory> findByCategoryName(String categoryName);

    /**
     * Найти все активные категории
     */
    List<IncidentCategory> findByIsActiveTrue();

    /**
     * Проверить существование категории по названию
     */
    boolean existsByCategoryName(String categoryName);
}




