package com.thoni.epimanager.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class NormalizationVerificationTest {

    @Autowired
    private DataDiagnosticService dataDiagnosticService;

    @Autowired
    private DataNormalizationService dataNormalizationService;

    @Test
    void verifyNormalization() {
        System.out.println(">>> ESTADO INICIAL <<<");
        dataDiagnosticService.runDiagnostic();

        System.out.println(">>> EXECUTANDO NORMALIZAÇÃO <<<");
        dataNormalizationService.normalizeData();

        System.out.println(">>> ESTADO FINAL <<<");
        dataDiagnosticService.runDiagnostic();
    }
}
