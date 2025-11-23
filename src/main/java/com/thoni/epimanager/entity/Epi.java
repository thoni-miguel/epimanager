package com.thoni.epimanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "epis")
public class Epi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String ca;

    @NotNull
    private Integer estoqueAtual;

    @NotNull
    private BigDecimal custoUnitario;

    private Integer limiteTrocaEmDias = 365; // Default 1 year if not specified

}
