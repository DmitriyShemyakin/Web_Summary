package com.example.web_summaryy.repository;

import com.example.web_summaryy.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("""
    select distinct r
    from Role r
    left join fetch r.directions
""")
    List<Role> findAllWithDirections();

    Optional<Role> findByTitle(String title);
}
