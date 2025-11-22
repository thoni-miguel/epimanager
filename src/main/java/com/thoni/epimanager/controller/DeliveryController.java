package com.thoni.epimanager.controller;

import com.thoni.epimanager.dto.DeliveryRequest;
import com.thoni.epimanager.entity.Delivery;
import com.thoni.epimanager.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Delivery create(@RequestBody @Valid DeliveryRequest request) {
        return deliveryService.createDelivery(
                request.employeeId(),
                request.epiId(),
                request.photoPath(),
                request.signaturePath());
    }
}
