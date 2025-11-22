package com.thoni.epimanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "epis")
public class Epi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String ca;

    @NotNull
    private Integer currentStock;

    @NotNull
    private BigDecimal unitCost;

    @ElementCollection
    @CollectionTable(name = "epi_allowed_roles", joinColumns = @JoinColumn(name = "epi_id"))
    @Column(name = "role")
    private List<String> allowedRoles = new ArrayList<>();
}
