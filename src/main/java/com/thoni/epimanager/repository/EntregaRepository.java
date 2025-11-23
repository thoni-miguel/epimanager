package com.thoni.epimanager.repository;

import com.thoni.epimanager.entity.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    @Query("SELECT e FROM Entrega e WHERE e.dataLimiteTroca <= :dataLimite AND e.dataDevolucao IS NULL")
    List<Entrega> findVencendoAte(@Param("dataLimite") LocalDate dataLimite);
}
