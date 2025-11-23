package com.thoni.epimanager.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class DataDiagnosticTest {

    @Autowired
    private DataDiagnosticService dataDiagnosticService;

    @Test
    void runDiagnostic() {
        dataDiagnosticService.runDiagnostic();
    }
}
