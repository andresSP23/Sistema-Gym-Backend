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
      c.createdDate >= COALESCE(:desde, c.createdDate)
     AND c.createdDate <= COALESCE(:hasta, c.createdDate)
""")
    Long totalClientes(@Param("desde") LocalDateTime desde,
                       @Param("hasta") LocalDateTime hasta);

}
