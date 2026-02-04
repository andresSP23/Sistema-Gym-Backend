package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoOperacionPago;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("pagos")
@Tag(name = "Pago")
@RequiredArgsConstructor
public class PagoController {

        private final PagoService service;

        @PostMapping
        @Operation(summary = "Registrar pago", description = "Registra una nueva transacción de pago.")
        @ApiResponse(responseCode = "200", description = "Pago registrado exitosamente")
        public PagoResponse registrarPago(@Valid @RequestBody PagoRequest request, Authentication connectedUser) {
                return service.registrarPago(request, connectedUser);
        }

        @GetMapping("/listar")
        @Operation(summary = "Buscar pagos", description = "Obtiene una lista paginada de pagos con filtros opcionales.")
        @ApiResponse(responseCode = "200", description = "Pagos obtenidos exitosamente")
        public ResponseEntity<PageResponse<PagoResponse>> findAll(

                        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
                        @RequestParam(name = "size", defaultValue = "10", required = false) int size,

                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,

                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,

                        @RequestParam(required = false) TipoOperacionPago tipoOperacion,

                        @RequestParam(required = false) MetodoPago metodo,

                        @RequestParam(required = false) Long clienteId,
                        @RequestParam(required = false) com.ansicode.SistemaAdministracionGym.enums.EstadoPago estado,
                        @RequestParam(required = false) String documento,
                        @RequestParam(required = false) String nombre,

                        @ParameterObject Pageable pageable) {

                Pageable finalPageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(Sort.Direction.DESC, "fechaPago"));

                return ResponseEntity.ok(
                                service.findAll(
                                                desde,
                                                hasta,
                                                tipoOperacion,
                                                metodo,
                                                clienteId,
                                                estado,
                                                documento,
                                                nombre,
                                                finalPageable));
        }

}
