package com.ansicode.SistemaAdministracionGym.asistencia;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("asistencias")
@Tag(name = "Asistencia")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @PostMapping("/registrar")
    public ResponseEntity<AsistenciaResponse> registrarAsistencia(
            @RequestBody @Valid AsistenciaRequest request
    ) {
        AsistenciaResponse response = asistenciaService.registrarPorCedula(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<PageResponse<AsistenciaResponse>> listarPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(name = "page", defaultValue = "0" ,required = false) int page,
            @RequestParam(name = "size", defaultValue = "10" ,required = false) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaEntrada").descending());
        PageResponse<AsistenciaResponse> response = asistenciaService.listarPorCliente(clienteId, pageable);
        return ResponseEntity.ok(response);
    }
}
