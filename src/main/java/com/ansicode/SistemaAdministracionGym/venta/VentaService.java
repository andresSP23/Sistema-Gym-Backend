package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteRepository;

import com.ansicode.SistemaAdministracionGym.detalleventa.*;
import com.ansicode.SistemaAdministracionGym.enums.EstadoVenta;
import com.ansicode.SistemaAdministracionGym.enums.TipoItemVenta;
import com.ansicode.SistemaAdministracionGym.movimientoinventario.MovimientoInventarioService;
import com.ansicode.SistemaAdministracionGym.producto.Producto;
import com.ansicode.SistemaAdministracionGym.producto.ProductoRepository;
import com.ansicode.SistemaAdministracionGym.servicio.Servicios;
import com.ansicode.SistemaAdministracionGym.servicio.ServiciosRepository;
import com.ansicode.SistemaAdministracionGym.sucursal.Sucursal;
import com.ansicode.SistemaAdministracionGym.sucursal.SucursalRepository;
import com.ansicode.SistemaAdministracionGym.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class    VentaService {



    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final SucursalRepository sucursalRepository;
    private final ServiciosRepository  serviciosRepository;
    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    private final VentaMapper ventaMapper;
    private final DetalleVentaMapper detalleVentaMapper;
    private final MovimientoInventarioService movimientoInventarioService;



    @Transactional
    public VentaResponse crearVentaServicio(CrearVentaServicioRequest request, Authentication connectedUser) {

        if (request.getCantidad() == null || request.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("cantidad debe ser mayor a 0");
        }

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada"));

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        Servicios servicio = serviciosRepository.findById(request.getServicioId())
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        BigDecimal cantidad = request.getCantidad(); // tu entidad maneja scale 3 en DetalleVenta

        Venta venta = new Venta();
        venta.setSucursal(sucursal);
        venta.setCliente(cliente);
        venta.setEstado(EstadoVenta.BORRADOR);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setNumeroFactura(generarNumeroFactura(sucursal)); // tu lógica
        venta.setCajeroUsuario((User) connectedUser.getPrincipal()); // si aplica

        // Detalle (snapshot)
        DetalleVenta det = new DetalleVenta();
        det.setTipoItem(TipoItemVenta.SERVICIO);
        det.setReferenciaId(servicio.getId());
        det.setDescripcionSnapshot(servicio.getNombre() + " - " + servicio.getDescripcion());
        det.setPrecioUnitarioSnapshot(servicio.getPrecio());
        det.setCantidad(cantidad);
        det.setDescuento(BigDecimal.ZERO);
        det.setImpuesto(BigDecimal.ZERO);

        // totalLinea dinero -> 2 decimales
        BigDecimal totalLinea = servicio.getPrecio()
                .multiply(cantidad)
                .setScale(2, RoundingMode.HALF_UP);

        det.setTotalLinea(totalLinea);

        venta.agregarDetalle(det);

        // Totales (dinero -> 2 decimales)
        venta.setSubtotal(totalLinea);
        venta.setDescuentoTotal(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        venta.setImpuestoTotal(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        venta.setTotal(totalLinea);

        Venta saved = ventaRepository.save(venta);
        return ventaMapper.toResponse(saved);
    }


    private String generarNumeroFactura(Sucursal sucursal) {
        String prefijo = "S" + sucursal.getId();

        // Tomar el último numeroFactura registrado para esa sucursal
        List<String> ultimos = ventaRepository.findUltimosNumerosFacturaPorSucursal(
                sucursal.getId(),
                PageRequest.of(0, 1)
        );

        long siguiente = 1L;
        if (!ultimos.isEmpty() && ultimos.get(0) != null) {
            String last = ultimos.get(0);

            // Espera formato: PREFIJO-00000001
            int dash = last.lastIndexOf('-');
            if (dash >= 0 && dash < last.length() - 1) {
                String parteNumerica = last.substring(dash + 1);
                if (parteNumerica.matches("\\d+")) {
                    siguiente = Long.parseLong(parteNumerica) + 1L;
                }
            }
        }

        return prefijo + "-" + String.format("%08d", siguiente);
    }




    @Transactional
    public VentaResponse crearVentaProductos(CrearVentaProductoRequest request, Authentication connectedUser) {

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada"));

        Cliente cliente = null;
        if (request.getClienteId() != null) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("items no puede estar vacío");
        }

        Venta venta = new Venta();
        venta.setSucursal(sucursal);
        venta.setCliente(cliente); // puede ser null (mostrador)
        venta.setEstado(EstadoVenta.BORRADOR);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setCajeroUsuario((User) connectedUser.getPrincipal()); // si aplica

        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemProductoRequest item : request.getItems()) {

            if (item.getProductoId() == null) {
                throw new IllegalArgumentException("productoId es obligatorio");
            }
            if (item.getCantidad() == null || item.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("cantidad debe ser mayor a 0");
            }

            // CAMBIA Producto por el nombre real de tu entidad/repo
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + item.getProductoId()));


            // Snapshot detalle
            DetalleVenta det = new DetalleVenta();
            det.setTipoItem(TipoItemVenta.PRODUCTO);
            det.setReferenciaId(producto.getId());
            det.setDescripcionSnapshot(producto.getNombre()); // ajusta si tu campo es nombre
            det.setPrecioUnitarioSnapshot(producto.getPrecioVenta()); // ajusta si tu campo es precio
            det.setCantidad(item.getCantidad());
            det.setDescuento(BigDecimal.ZERO);
            det.setImpuesto(BigDecimal.ZERO);

            BigDecimal totalLinea = producto.getPrecioVenta()
                    .multiply(item.getCantidad())
                    .setScale(2, RoundingMode.HALF_UP);

            det.setTotalLinea(totalLinea);

            venta.agregarDetalle(det);

            subtotal = subtotal.add(totalLinea);
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        venta.setSubtotal(subtotal);
        venta.setDescuentoTotal(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        venta.setImpuestoTotal(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        venta.setTotal(subtotal);

        // Generar numeroFactura con retry anti-colisión (uq_ventas_sucursal_factura)
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            venta.setNumeroFactura(generarNumeroFactura(sucursal));
            try {
                Venta saved = ventaRepository.saveAndFlush(venta);
                return ventaMapper.toResponse(saved);
            } catch (org.springframework.dao.DataIntegrityViolationException ex) {
                if (i == maxRetries - 1) throw ex;
            }
        }

        throw new IllegalStateException("No se pudo generar un número de factura único");
    }
}
