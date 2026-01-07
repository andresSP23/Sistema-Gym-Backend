package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.user.User;
import org.springframework.stereotype.Service;

@Service
public class VentaMapper {


    public Venta toVenta(VentaRequest request, Cliente cliente, User vendedor) {
        Venta venta = new Venta();

        venta.setCliente(cliente);
        venta.setVendedor(vendedor);
        venta.setTotal(request.getTotal());
        venta.setEstadoVenta(request.getEstadoVenta());
        venta.setFechaVenta(request.getFechaVenta());

        return venta;
    }

    public void updateVenta(Venta venta, VentaRequest request, Cliente cliente, User vendedor) {
        venta.setCliente(cliente);
        venta.setVendedor(vendedor);
        venta.setTotal(request.getTotal());
        venta.setEstadoVenta(request.getEstadoVenta());
        venta.setFechaVenta(request.getFechaVenta());
    }

    public VentaResponse toVentaResponse(Venta venta) {
        VentaResponse response = new VentaResponse();

        response.setId(venta.getId());

        response.setClienteId(venta.getCliente().getId());
        response.setClienteNombre(
                venta.getCliente().getNombres() + " " + venta.getCliente().getApellidos()
        );

        response.setVendedorId(venta.getVendedor().getId());
        response.setVendedorNombre(
                venta.getVendedor().fullname()
        );

        response.setTotal(venta.getTotal());
        response.setEstadoVenta(venta.getEstadoVenta());
        response.setFechaVenta(venta.getFechaVenta());

        // viene del BaseEntity (borrado lógico)
        response.setActivo(venta.getActivo());

        return response;
    }
}
