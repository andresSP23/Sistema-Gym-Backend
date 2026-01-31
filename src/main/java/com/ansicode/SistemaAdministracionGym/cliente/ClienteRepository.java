package com.ansicode.SistemaAdministracionGym.cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCedula(String cedula);

    boolean existsByCedula(String cedula);

    boolean existsByEmail(String email);

    boolean existsByTelefono(String telefono);

    // Methods for UI filtering (Active only)
    org.springframework.data.domain.Page<Cliente> findAllByIsVisibleTrue(
            org.springframework.data.domain.Pageable pageable);

    Optional<Cliente> findByCedulaAndIsVisibleTrue(String cedula);
}
