package com.ansicode.SistemaAdministracionGym.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional <User> findByTelefono(String telefono);
}
