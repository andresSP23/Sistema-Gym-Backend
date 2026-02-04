package com.ansicode.SistemaAdministracionGym;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:schema-gen.properties")
public class SchemaGenTest {

    @Test
    void generateSchema() {
        // The context load triggers schema generation via properties
    }
}
