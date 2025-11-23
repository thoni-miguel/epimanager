package com.thoni.epimanager.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "atividades_epis")
public class AtividadeEpi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "atividade_id", nullable = false)
    private Atividade atividade;

    @ManyToOne
    @JoinColumn(name = "epi_id", nullable = false)
    private Epi epi;

    // Se nulo, é um EPI padrão. Se preenchido (ex: "Ruído"), é condicional.
    private String condicao;
}
