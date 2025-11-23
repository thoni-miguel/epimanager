package com.thoni.epimanager.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.Commit;

@SpringBootTest
@ActiveProfiles("test")
public class DataNormalizationTest {

    @Autowired
    private DataNormalizationService dataNormalizationService;

    @Test
    @Commit // Ensure changes are committed to the database (if using a real DB, though
            // @DataJpaTest usually rolls back. Here we use @SpringBootTest)
    void runNormalization() {
        dataNormalizationService.normalizeData();
    }
}
