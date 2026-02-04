package com.ansicode.SistemaAdministracionGym.contrato;

import com.ansicode.SistemaAdministracionGym.enums.EstadoContrato;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * Specifications para filtros dinámicos de Contrato.
 */
public class ContratoSpecifications {

    /**
     * Filtro por estado del contrato.
     */
    public static Specification<Contrato> estado(EstadoContrato estado) {
        return (root, query, cb) -> {
            if (estado == null)
                return null;
            return cb.equal(root.get("estadoContrato"), estado);
        };
    }

    /**
     * Filtro por cliente - búsqueda en nombre, apellido, cédula o email.
     */
    public static Specification<Contrato> clienteBusqueda(String busqueda) {
        return (root, query, cb) -> {
            if (busqueda == null || busqueda.isBlank())
                return null;

            Join<Object, Object> cliente = root.join("cliente");
            String pattern = "%" + busqueda.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(cliente.get("nombres")), pattern),
                    cb.like(cb.lower(cliente.get("apellidos")), pattern),
                    cb.like(cb.lower(cliente.get("cedula")), pattern),
                    cb.like(cb.lower(cliente.get("email")), pattern));
        };
    }

    /**
     * Filtro por rango de fechas de creación.
     */
    public static Specification<Contrato> fechaDesde(LocalDateTime desde) {
        return (root, query, cb) -> {
            if (desde == null)
                return null;
            return cb.greaterThanOrEqualTo(root.get("createdAt"), desde);
        };
    }

    public static Specification<Contrato> fechaHasta(LocalDateTime hasta) {
        return (root, query, cb) -> {
            if (hasta == null)
                return null;
            return cb.lessThan(root.get("createdAt"), hasta);
        };
    }

    /**
     * Filtro combinado.
     */
    public static Specification<Contrato> conFiltros(
            EstadoContrato estado,
            String clienteBusqueda,
            LocalDateTime desde,
            LocalDateTime hasta) {

        return Specification.allOf(
                estado(estado),
                clienteBusqueda(clienteBusqueda),
                fechaDesde(desde),
                fechaHasta(hasta));
    }
}
