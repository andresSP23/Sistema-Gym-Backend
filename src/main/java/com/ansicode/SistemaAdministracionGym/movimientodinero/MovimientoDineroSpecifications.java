package com.ansicode.SistemaAdministracionGym.movimientodinero;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MovimientoDineroSpecifications {

    public static Specification<MovimientoDinero> tipo(String tipo) {
        return (root, query, cb) ->
                (tipo == null || tipo.isBlank()) ? cb.conjunction() : cb.equal(root.get("tipo"), tipo);
    }

    public static Specification<MovimientoDinero> concepto(String concepto) {
        return (root, query, cb) ->
                (concepto == null || concepto.isBlank()) ? cb.conjunction() : cb.equal(root.get("concepto"), concepto);
    }

    public static Specification<MovimientoDinero> metodo(String metodo) {
        return (root, query, cb) ->
                (metodo == null || metodo.isBlank()) ? cb.conjunction() : cb.equal(root.get("metodo"), metodo);
    }

    public static Specification<MovimientoDinero> moneda(String moneda) {
        return (root, query, cb) ->
                (moneda == null || moneda.isBlank()) ? cb.conjunction() : cb.equal(root.get("moneda"), moneda);
    }

    public static Specification<MovimientoDinero> usuarioId(Long usuarioId) {
        return (root, query, cb) ->
                (usuarioId == null) ? cb.conjunction() : cb.equal(root.get("usuarioId"), usuarioId);
        // Si en tu entity es root.get("usuario").get("id") cambia esa línea.
    }

    public static Specification<MovimientoDinero> fechaDesde(LocalDateTime desde) {
        return (root, query, cb) ->
                (desde == null) ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("fecha"), desde);
    }

    public static Specification<MovimientoDinero> fechaHasta(LocalDateTime hasta) {
        return (root, query, cb) ->
                (hasta == null) ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("fecha"), hasta);
    }
}
