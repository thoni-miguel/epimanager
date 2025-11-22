package com.thoni.epimanager.controller;

import com.thoni.epimanager.entity.Employee;
import com.thoni.epimanager.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public List<Employee> listAll() {
        return employeeRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee create(@RequestBody @Valid Employee employee) {
        return employeeRepository.save(employee);
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }
}
