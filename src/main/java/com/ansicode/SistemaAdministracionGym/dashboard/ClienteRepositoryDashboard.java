package com.ansicode.SistemaAdministracionGym.dashboard;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ClienteRepositoryDashboard extends JpaRepository<Cliente, Long> {

    @Query("""
               SELECT COUNT(c.id)
               FROM Cliente c
               WHERE
                  c.createdAt >= COALESCE(:desde, c.createdAt)
                 AND c.createdAt <= COALESCE(:hasta, c.createdAt)
            """)
    Long totalClientes(@Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);

}
