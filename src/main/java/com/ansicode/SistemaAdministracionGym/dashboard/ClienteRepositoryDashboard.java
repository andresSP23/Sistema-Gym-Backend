package com.ansicode.SistemaAdministracionGym.dashboard;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClienteRepositoryDashboard extends JpaRepository<Cliente, Long> {


    @Query("SELECT COUNT(c.id) FROM Cliente c")
    Long totalClientes();

}
