package com.ansicode.SistemaAdministracionGym.movimientoinventario;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("movimiento-inventario")
@Tag(name = "Movimiento Inventario")
@RequiredArgsConstructor
public class MovimientoInventarioController {

    private final MovimientoInventarioService service;


    @GetMapping("/movimientos/{productoId}")
    public ResponseEntity<List<MovimientoInventario>> listarPorProducto(
            @PathVariable Long productoId
    ) {
        return ResponseEntity.ok(
                service.listarPorProducto(productoId)
        );
    }

}
