package com.thoni.epimanager.repository;

import com.thoni.epimanager.entity.Epi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpiRepository extends JpaRepository<Epi, Long> {

    @Query("SELECT e FROM Epi e JOIN e.allowedRoles r WHERE r = :role")
    List<Epi> findByAllowedRole(@Param("role") String role);
}
