package com.ansicode.SistemaAdministracionGym.servicio;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("servicios")
@RequiredArgsConstructor
@Tag(name = "Servicio")
public class ServiciosController {

    private final ServicioService serviciosService;


    @PostMapping("/crear-servicio")
    public ResponseEntity<ServiciosResponse> create(
            @RequestBody @Valid ServiciosRequest request
    ) {
        return ResponseEntity.ok(serviciosService.create(request));
    }

    @GetMapping("/findAll")
    public ResponseEntity<PageResponse<ServiciosResponse>> findAll(
            @RequestParam(name = "suscripcion") Boolean suscripcion,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(serviciosService.list(suscripcion, pageable));
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<ServiciosResponse> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(serviciosService.findById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ServiciosResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ServiciosRequest request
    ) {
        return ResponseEntity.ok(serviciosService.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        serviciosService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/combo")
    public ResponseEntity<List<ServiciosResponse>> combo(
            @RequestParam(name = "suscripcion") Boolean suscripcion
    ) {
        return ResponseEntity.ok(serviciosService.combo(suscripcion));
    }

}
