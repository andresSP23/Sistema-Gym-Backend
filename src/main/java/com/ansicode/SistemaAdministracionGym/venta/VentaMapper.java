package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.user.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaMapper {

    public Venta toVenta(Cliente cliente, User vendedor, LocalDateTime fechaVenta , MetodoPago metodoPago
    ) {
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setVendedor(vendedor);
        venta.setFechaVenta(fechaVenta);
        venta.setMetodoPago(metodoPago);
        venta.setActivo(true);
        return venta;
    }

    public VentaResponse toVentaResponse(Venta venta, List<DetalleVentaResponse> detalles) {
        VentaResponse response = new VentaResponse();

        response.setId(venta.getId());
        response.setClienteId(venta.getCliente().getId());
        response.setClienteNombre(
                venta.getCliente().getNombres() + " " + venta.getCliente().getApellidos()
        );

        response.setVendedorId(venta.getVendedor().getId());
        response.setVendedorNombre(venta.getVendedor().fullname());

        response.setTotal(venta.getTotal());
        response.setMetodoPago(venta.getMetodoPago());
        response.setFechaVenta(venta.getFechaVenta());
        response.setDetalles(detalles);

        return response;
    }

}
