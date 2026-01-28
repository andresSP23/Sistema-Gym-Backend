package com.ansicode.SistemaAdministracionGym.movimientoinventario;

import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoInventario;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MovimientoInventarioSpecifications {

    public static Specification<MovimientoInventario> productoId(Long productoId) {
        return (root, query, cb) ->
                (productoId == null)
                        ? cb.conjunction()
                        : cb.equal(root.get("producto").get("id"), productoId);
    }

    public static Specification<MovimientoInventario> tipoMovimiento(String tipo) {
        return (root, query, cb) -> {
            if (tipo == null || tipo.isBlank()) return cb.conjunction();
            try {
                TipoMovimientoInventario t = TipoMovimientoInventario.valueOf(tipo.trim().toUpperCase());
                return cb.equal(root.get("tipoMovimiento"), t);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("tipoMovimiento inválido: " + tipo);
            }
        };
    }

    public static Specification<MovimientoInventario> fechaDesde(LocalDateTime desde) {
        return (root, query, cb) ->
                (desde == null)
                        ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("createdAt"), desde);
    }

    public static Specification<MovimientoInventario> fechaHasta(LocalDateTime hasta) {
        return (root, query, cb) ->
                (hasta == null)
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get("createdAt"), hasta);
    }

    public static Specification<MovimientoInventario> createdBy(Long userId) {
        return (root, query, cb) ->
                (userId == null)
                        ? cb.conjunction()
                        : cb.equal(root.get("createdBy"), userId);
    }

    public static Specification<MovimientoInventario> cantidadMin(Integer min) {
        return (root, query, cb) ->
                (min == null)
                        ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("cantidad"), min);
    }

    public static Specification<MovimientoInventario> cantidadMax(Integer max) {
        return (root, query, cb) ->
                (max == null)
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get("cantidad"), max);
    }

    public static Specification<MovimientoInventario> stockActualMin(Integer min) {
        return (root, query, cb) ->
                (min == null)
                        ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("stockActual"), min);
    }

    public static Specification<MovimientoInventario> stockActualMax(Integer max) {
        return (root, query, cb) ->
                (max == null)
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get("stockActual"), max);
    }

    public static Specification<MovimientoInventario> search(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return cb.conjunction();
            String like = "%" + q.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("observacion")), like);
        };
    }
}
