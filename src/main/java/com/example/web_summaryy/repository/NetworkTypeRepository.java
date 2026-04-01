package com.example.web_summaryy.repository;

import com.example.web_summaryy.model.NetworkType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NetworkTypeRepository extends JpaRepository<NetworkType, Long> {

    Optional<NetworkType> findByCode(String code);

    boolean existsByCode(String code);
}
