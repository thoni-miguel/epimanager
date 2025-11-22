package com.thoni.epimanager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "epi_id", nullable = false)
    private Epi epi;

    @Column(nullable = false)
    private LocalDateTime deliveryDate;

    private String signaturePath;
    private String photoPath;
}
