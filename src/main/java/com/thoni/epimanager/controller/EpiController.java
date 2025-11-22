package com.thoni.epimanager.controller;

import com.thoni.epimanager.entity.Epi;
import com.thoni.epimanager.service.EpiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/epis")
public class EpiController {

    private final EpiService epiService;

    public EpiController(EpiService epiService) {
        this.epiService = epiService;
    }

    @GetMapping
    public List<Epi> listAll() {
        return epiService.findAll();
    }

    @GetMapping("/recommended")
    public List<Epi> listRecommended(@RequestParam String role) {
        return epiService.findRecommendedForRole(role);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Epi create(@RequestBody @Valid Epi epi) {
        return epiService.save(epi);
    }
}
