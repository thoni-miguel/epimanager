package com.thoni.epimanager.dto;

import jakarta.validation.constraints.NotNull;

public record DeliveryRequest(
        @NotNull Long employeeId,
        @NotNull Long epiId,
        String photoPath,
        String signaturePath) {
}
