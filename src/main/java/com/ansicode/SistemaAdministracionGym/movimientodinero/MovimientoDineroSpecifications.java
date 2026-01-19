package com.ansicode.SistemaAdministracionGym.movimientodinero;

import com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoDinero;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoDinero;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MovimientoDineroSpecifications {
    public static Specification<MovimientoDinero> tipo(String tipo) {
        return (root, query, cb) -> {
            if (tipo == null || tipo.isBlank()) return cb.conjunction();
            try {
                TipoMovimientoDinero t = TipoMovimientoDinero.valueOf(tipo.trim().toUpperCase());
                return cb.equal(root.get("tipo"), t);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("tipo inválido: " + tipo);
            }
        };
    }

    public static Specification<MovimientoDinero> concepto(String concepto) {
        return (root, query, cb) -> {
            if (concepto == null || concepto.isBlank()) return cb.conjunction();
            try {
                ConceptoMovimientoDinero c = ConceptoMovimientoDinero.valueOf(concepto.trim().toUpperCase());
                return cb.equal(root.get("concepto"), c);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("concepto inválido: " + concepto);
            }
        };
    }

    public static Specification<MovimientoDinero> metodo(String metodo) {
        return (root, query, cb) -> {
            if (metodo == null || metodo.isBlank()) return cb.conjunction();
            try {
                MetodoPago m = MetodoPago.valueOf(metodo.trim().toUpperCase());
                return cb.equal(root.get("metodo"), m);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("metodo inválido: " + metodo);
            }
        };
    }

    public static Specification<MovimientoDinero> moneda(String moneda) {
        return (root, query, cb) ->
                (moneda == null || moneda.isBlank())
                        ? cb.conjunction()
                        : cb.equal(root.get("moneda"), moneda.trim().toUpperCase());
    }

    public static Specification<MovimientoDinero> usuarioId(Long usuarioId) {
        return (root, query, cb) ->
                (usuarioId == null)
                        ? cb.conjunction()
                        : cb.equal(root.get("usuarioId"), usuarioId);
    }

    public static Specification<MovimientoDinero> fechaDesde(LocalDateTime desde) {
        return (root, query, cb) ->
                (desde == null)
                        ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("fecha"), desde);
    }

    public static Specification<MovimientoDinero> fechaHasta(LocalDateTime hasta) {
        return (root, query, cb) ->
                (hasta == null)
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get("fecha"), hasta);
    }

    // (Opcional útil) filtrar por sesión caja
    public static Specification<MovimientoDinero> sesionCajaId(Long sesionCajaId) {
        return (root, query, cb) ->
                (sesionCajaId == null)
                        ? cb.conjunction()
                        : cb.equal(root.get("sesionCaja").get("id"), sesionCajaId);
    }
}
