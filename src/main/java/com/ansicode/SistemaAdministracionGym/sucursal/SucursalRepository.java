package com.ansicode.SistemaAdministracionGym.sucursal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    // Buscar la sucursal activa (asumiendo que solo habrá una)
    Optional<Sucursal> findFirstByIsVisibleTrue();

    // Opcional: buscar por código si quieres control extra
    Optional<Sucursal> findByCodigoSucursal(String codigoSucursal);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT s FROM Sucursal s WHERE s.id = :id")
    Optional<Sucursal> findByIdWithLock(@org.springframework.data.repository.query.Param("id") Long id);
}
