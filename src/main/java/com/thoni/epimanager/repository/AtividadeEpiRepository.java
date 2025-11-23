package com.thoni.epimanager.repository;

import com.thoni.epimanager.entity.AtividadeEpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtividadeEpiRepository extends JpaRepository<AtividadeEpi, Long> {
    List<AtividadeEpi> findByAtividadeId(Long atividadeId);
}
