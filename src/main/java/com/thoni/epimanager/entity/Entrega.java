package com.thoni.epimanager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "entregas")
public class Entrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @ManyToOne
    @JoinColumn(name = "epi_id", nullable = false)
    private Epi epi;

    @Column(nullable = false)
    private LocalDateTime dataEntrega;

    private String assinaturaPath;
    private String fotoPath;
}
