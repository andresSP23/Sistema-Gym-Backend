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
            String sqlDrop = "ALTER TABLE movimientos_dinero DROP CONSTRAINT IF EXISTS movimientos_dinero_metodo_check";
            jdbcTemplate.execute(sqlDrop);

            // Recrearla con el nuevo valor (Agregando OTRO)
            String sqlAdd = "ALTER TABLE movimientos_dinero ADD CONSTRAINT movimientos_dinero_metodo_check CHECK (metodo IN ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'OTRO'))";
            jdbcTemplate.execute(sqlAdd);

            System.out.println(
                    "✅ Restricción de base de datos actualizada correctamente: movimientos_dinero_metodo_check");
        } catch (Exception e) {
            System.out.println("⚠️ Advertencia al actualizar DB: " + e.getMessage());
        }
    }
}
