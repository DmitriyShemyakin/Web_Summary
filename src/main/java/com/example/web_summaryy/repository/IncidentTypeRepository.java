// com.example.web_summaryy.repository.IncidentTypeRepository

package com.example.web_summaryy.repository;

import com.example.web_summaryy.model.IncidentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository для работы со справочником типов аварий
 */
@Repository
public interface IncidentTypeRepository extends JpaRepository<IncidentType, Long> {

    /**
     * Найти тип по коду
     */
    Optional<IncidentType> findByTypeCode(String typeCode);

    /**
     * Найти все активные типы
     */
    List<IncidentType> findByIsActiveTrue();

    /**
     * Проверить существование типа по коду
     */
    boolean existsByTypeCode(String typeCode);
}




