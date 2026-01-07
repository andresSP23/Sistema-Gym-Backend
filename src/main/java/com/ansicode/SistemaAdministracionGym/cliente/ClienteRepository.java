package com.ansicode.SistemaAdministracionGym.cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {


    Optional<Cliente> findByCedula(String cedula);

    boolean existsByCedula(String cedula);
}
