package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.enums.EstadoPago;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoOperacionPago;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class PagoSpecifications {

    // Helper to reuse "cliente" join if it already exists in the query
    private static Join<Pago, Cliente> getClienteJoin(jakarta.persistence.criteria.Root<Pago> root) {
        for (Join<Pago, ?> join : root.getJoins()) {
            if ("cliente".equals(join.getAttribute().getName())) {
                return (Join<Pago, Cliente>) join;
            }
        }
        return root.join("cliente", JoinType.LEFT);
    }

    public static Specification<Pago> clienteId(Long clienteId) {
        return (root, query, cb) -> {
            if (clienteId == null)
                return cb.conjunction();
            // Direct ID check without join (if possible) or ensure proper join
            return cb.equal(root.get("cliente").get("id"), clienteId);
        };
    }

    public static Specification<Pago> estado(EstadoPago estado) {
        return (root, query, cb) -> (estado == null)
                ? cb.conjunction()
                : cb.equal(root.get("estado"), estado);
    }

    public static Specification<Pago> documento(String documento) {
        return (root, query, cb) -> {
            if (documento == null || documento.isBlank()) {
                return cb.conjunction();
            }
            Join<Pago, Cliente> clienteJoin = getClienteJoin(root);
            return cb.like(clienteJoin.get("cedula"), "%" + documento + "%");
        };
    }

    public static Specification<Pago> nombre(String nombre) {
        return (root, query, cb) -> {
            if (nombre == null || nombre.isBlank()) {
                return cb.conjunction();
            }
            Join<Pago, Cliente> clienteJoin = getClienteJoin(root);
            String pattern = "%" + nombre.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(clienteJoin.get("nombres")), pattern),
                    cb.like(cb.lower(clienteJoin.get("apellidos")), pattern));
        };
    }

    public static Specification<Pago> fechaDesde(LocalDateTime desde) {
        return (root, query, cb) -> (desde == null)
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("fechaPago"), desde);
    }

    public static Specification<Pago> fechaHasta(LocalDateTime hasta) {
        return (root, query, cb) -> (hasta == null)
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("fechaPago"), hasta);
    }

    public static Specification<Pago> tipoOperacion(TipoOperacionPago tipoOperacion) {
        return (root, query, cb) -> (tipoOperacion == null)
                ? cb.conjunction()
                : cb.equal(root.get("tipoOperacion"), tipoOperacion);
    }

    public static Specification<Pago> metodo(MetodoPago metodo) {
        return (root, query, cb) -> (metodo == null)
                ? cb.conjunction()
                : cb.equal(root.get("metodo"), metodo);
    }
}
