package com.ansicode.SistemaAdministracionGym.gasto;

import com.ansicode.SistemaAdministracionGym.enums.CategoriaGasto;
import com.ansicode.SistemaAdministracionGym.enums.EstadoGasto;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class GastoSpecifications {

    public static Specification<Gasto> nombre(String nombre) {
        return (root, query, cb) -> {
            if (nombre == null || nombre.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + nombre.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("nombre")), pattern),
                    cb.like(cb.lower(root.get("descripcion")), pattern));
        };
    }

    public static Specification<Gasto> fechaDesde(LocalDate desde) {
        return (root, query, cb) -> (desde == null)
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("fechaGasto"), desde);
    }

    public static Specification<Gasto> fechaHasta(LocalDate hasta) {
        return (root, query, cb) -> (hasta == null)
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("fechaGasto"), hasta);
    }

    public static Specification<Gasto> estado(EstadoGasto estado) {
        return (root, query, cb) -> (estado == null)
                ? cb.conjunction()
                : cb.equal(root.get("estado"), estado);
    }

    public static Specification<Gasto> categoria(CategoriaGasto categoria) {
        return (root, query, cb) -> (categoria == null)
                ? cb.conjunction()
                : cb.equal(root.get("categoria"), categoria);
    }

    public static Specification<Gasto> metodoPago(MetodoPago metodoPago) {
        return (root, query, cb) -> (metodoPago == null)
                ? cb.conjunction()
                : cb.equal(root.get("metodoPago"), metodoPago);
    }
}
