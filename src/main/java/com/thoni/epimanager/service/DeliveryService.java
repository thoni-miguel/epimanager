package com.thoni.epimanager.service;

import com.thoni.epimanager.entity.Delivery;
import com.thoni.epimanager.entity.Employee;
import com.thoni.epimanager.entity.Epi;
import com.thoni.epimanager.repository.DeliveryRepository;
import com.thoni.epimanager.repository.EmployeeRepository;
import com.thoni.epimanager.repository.EpiRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final EmployeeRepository employeeRepository;
    private final EpiRepository epiRepository;

    public DeliveryService(DeliveryRepository deliveryRepository, EmployeeRepository employeeRepository,
            EpiRepository epiRepository) {
        this.deliveryRepository = deliveryRepository;
        this.employeeRepository = employeeRepository;
        this.epiRepository = epiRepository;
    }

    @Transactional
    public Delivery createDelivery(Long employeeId, Long epiId, String photoPath, String signaturePath) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Epi epi = epiRepository.findById(epiId)
                .orElseThrow(() -> new RuntimeException("EPI not found"));

        if (epi.getCurrentStock() <= 0) {
            throw new RuntimeException("EPI out of stock");
        }

        // Decrement stock
        epi.setCurrentStock(epi.getCurrentStock() - 1);
        epiRepository.save(epi);

        // Create delivery record
        Delivery delivery = new Delivery();
        delivery.setEmployee(employee);
        delivery.setEpi(epi);
        delivery.setDeliveryDate(LocalDateTime.now());
        delivery.setPhotoPath(photoPath);
        delivery.setSignaturePath(signaturePath);

        return deliveryRepository.save(delivery);
    }
}
