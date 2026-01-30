package com.ansicode.SistemaAdministracionGym.clientesuscripcion;

import com.ansicode.SistemaAdministracionGym.enums.EstadoSuscripcion;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ClienteSuscripcionSpecifications {


    public static Specification<ClienteSuscripcion> clienteId(Long clienteId) {
        return (root, query, cb) ->
                (clienteId == null)
                        ? cb.conjunction()
                        : cb.equal(root.get("cliente").get("id"), clienteId);
    }

    public static Specification<ClienteSuscripcion> servicioId(Long servicioId) {
        return (root, query, cb) ->
                (servicioId == null)
                        ? cb.conjunction()
                        : cb.equal(root.get("servicio").get("id"), servicioId);
    }

    public static Specification<ClienteSuscripcion> estado(String estado) {
        return (root, query, cb) -> {
            if (estado == null || estado.isBlank()) return cb.conjunction();
            try {
                EstadoSuscripcion e = EstadoSuscripcion.valueOf(estado.trim().toUpperCase());
                return cb.equal(root.get("estado"), e);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("estado inválido: " + estado);
            }
        };
    }

    public static Specification<ClienteSuscripcion> vigente(Boolean vigente) {
        return (root, query, cb) -> {
            if (vigente == null) return cb.conjunction();

            LocalDateTime now = LocalDateTime.now();

            return vigente
                    ? cb.greaterThan(root.get("fechaFin"), now)
                    : cb.lessThanOrEqualTo(root.get("fechaFin"), now);
        };
    }

    public static Specification<ClienteSuscripcion> fechaInicioDesde(LocalDateTime desde) {
        return (root, query, cb) ->
                (desde == null)
                        ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("fechaInicio"), desde);
    }

    public static Specification<ClienteSuscripcion> fechaInicioHasta(LocalDateTime hasta) {
        return (root, query, cb) ->
                (hasta == null)
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get("fechaInicio"), hasta);
    }

    // opcional: por venta
    public static Specification<ClienteSuscripcion> ventaId(Long ventaId) {
        return (root, query, cb) ->
                (ventaId == null)
                        ? cb.conjunction()
                        : cb.equal(root.get("venta").get("id"), ventaId);
    }
}
