package com.ansicode.SistemaAdministracionGym.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseFixer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Eliminar la restricción antigua
            // Nota: En PostgreSQL los constraints de check suelen tener nombres
            // autogenerados o específicos.
            // El error nos dio el nombre: contratos_estado_contrato_check
            String sqlDrop = "ALTER TABLE contratos DROP CONSTRAINT IF EXISTS contratos_estado_contrato_check";
            jdbcTemplate.execute(sqlDrop);

            // Recrearla con el nuevo valor
            String sqlAdd = "ALTER TABLE contratos ADD CONSTRAINT contratos_estado_contrato_check CHECK (estado_contrato IN ('ACTIVO', 'FINALIZADO', 'CANCELADO', 'PENDIENTE'))";
            jdbcTemplate.execute(sqlAdd);

            System.out.println(
                    "✅ Restricción de base de datos actualizada correctamente: contratos_estado_contrato_check");
        } catch (Exception e) {
            System.out.println("⚠️ Advertencia al actualizar DB: " + e.getMessage());
            // No lanzamos error para no detener la app si ya estaba arreglado o falla por
            // algo menor
        }
    }
}
