package com.ansicode.SistemaAdministracionGym.banco.movimiento;

import com.ansicode.SistemaAdministracionGym.enums.ConceptoMovimientoBanco;
import com.ansicode.SistemaAdministracionGym.enums.TipoMovimientoBanco;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Specifications para filtros dinámicos de MovimientoBanco.
 */
public class MovimientoBancoSpecifications {

    public static Specification<MovimientoBanco> conFiltros(
            Long bancoId,
            TipoMovimientoBanco tipo,
            ConceptoMovimientoBanco concepto,
            LocalDateTime desde,
            LocalDateTime hasta,
            String textoLibre) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por banco (obligatorio)
            if (bancoId != null) {
                predicates.add(cb.equal(root.get("banco").get("id"), bancoId));
            }

            // Filtro por tipo de movimiento
            if (tipo != null) {
                predicates.add(cb.equal(root.get("tipo"), tipo));
            }

            // Filtro por concepto
            if (concepto != null) {
                predicates.add(cb.equal(root.get("concepto"), concepto));
            }

            // Filtro por rango de fechas
            if (desde != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fecha"), desde));
            }
            if (hasta != null) {
                predicates.add(cb.lessThan(root.get("fecha"), hasta));
            }

            // Búsqueda de texto libre en descripcion y referencia
            if (textoLibre != null && !textoLibre.isBlank()) {
                String pattern = "%" + textoLibre.toLowerCase() + "%";
                Predicate descripcionLike = cb.like(cb.lower(root.get("descripcion")), pattern);
                Predicate referenciaLike = cb.like(cb.lower(root.get("referencia")), pattern);
                predicates.add(cb.or(descripcionLike, referenciaLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
