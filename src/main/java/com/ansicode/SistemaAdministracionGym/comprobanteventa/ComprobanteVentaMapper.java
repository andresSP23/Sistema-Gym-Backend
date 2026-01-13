package com.ansicode.SistemaAdministracionGym.comprobanteventa;

import com.ansicode.SistemaAdministracionGym.sucursal.Sucursal;
import com.ansicode.SistemaAdministracionGym.venta.VentaResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class ComprobanteVentaMapper {

    public ComprobanteVentaResponse fromVentaResponse(VentaResponse ventaResponse, Sucursal sucursal) {
        ComprobanteVentaResponse response = new ComprobanteVentaResponse();
        response.setVentaId(ventaResponse.getId());
        response.setClienteId(ventaResponse.getClienteId());
        response.setClienteNombre(ventaResponse.getClienteNombre());
        response.setVendedorId(ventaResponse.getVendedorId());
        response.setVendedorNombre(ventaResponse.getVendedorNombre());
        response.setMetodoPago(ventaResponse.getMetodoPago());
        response.setTotal(ventaResponse.getTotal());
        response.setFechaVenta(ventaResponse.getFechaVenta());
        response.setDetalles(ventaResponse.getDetalles());
        response.setSucursalNombre(sucursal.getNombre());
        response.setSucursalDireccion(sucursal.getDireccion());
        response.setFechaGeneracion(LocalDateTime.now());
        return response;
    }
}
