package com.example.web_summaryy.repository;

import com.example.web_summaryy.model.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    Optional<Position> findByPositionUUID(String positionUUID);

    Page<Position> findByPositionNameforbsContainingIgnoreCase(String namePart, Pageable pageable);
}
