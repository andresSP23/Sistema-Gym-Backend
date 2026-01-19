package com.ansicode.SistemaAdministracionGym.cuadrecaja;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuadreCajaRepository  extends JpaRepository<CuadreCaja , Long> {

    Optional<CuadreCaja> findBySesionCaja_Id(Long sesionCajaId);


}
