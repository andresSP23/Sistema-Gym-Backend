package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteRepository;

import com.ansicode.SistemaAdministracionGym.detalleventa.*;
import com.ansicode.SistemaAdministracionGym.enums.EstadoVenta;
import com.ansicode.SistemaAdministracionGym.enums.TipoItemVenta;
import com.ansicode.SistemaAdministracionGym.handler.BusinessErrorCodes;
import com.ansicode.SistemaAdministracionGym.handler.BussinessException;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final SucursalRepository sucursalRepository;
    private final ServiciosRepository serviciosRepository;
    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    private final VentaMapper ventaMapper;
    private final DetalleVentaMapper detalleVentaMapper;
    private final MovimientoInventarioService movimientoInventarioService;

    @Transactional
    public VentaResponse crearVentaServicio(CrearVentaServicioRequest request, Authentication connectedUser) {

        if (request == null) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }
        if (request.getSucursalId() == null) {
            throw new BussinessException(BusinessErrorCodes.VENTA_SUCURSAL_REQUIRED);
        }
        if (request.getClienteId() == null) {
            throw new BussinessException(BusinessErrorCodes.VENTA_CLIENTE_REQUIRED_PARA_SERVICIO);
        }
        if (request.getServicioId() == null) {
            throw new BussinessException(BusinessErrorCodes.VENTA_SERVICIO_REQUIRED);
        }
        if (request.getCantidad() == null || request.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BussinessException(BusinessErrorCodes.VENTA_CANTIDAD_INVALIDA);
        }
        if (connectedUser == null || connectedUser.getPrincipal() == null) {
            throw new BussinessException(BusinessErrorCodes.BAD_CREDENTIALS);
        }

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.VENTA_SUCURSAL_NOT_FOUND));

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.VENTA_CLIENTE_NOT_FOUND));

        Servicios servicio = serviciosRepository.findById(request.getServicioId())
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.VENTA_SERVICIO_NOT_FOUND));

        if (servicio.getPrecio() == null || servicio.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BussinessException(BusinessErrorCodes.VENTA_SERVICIO_PRECIO_INVALIDO);
        }

        BigDecimal cantidad = request.getCantidad();

        Venta venta = new Venta();
        venta.setSucursal(sucursal);
        venta.setCliente(cliente);
        venta.setEstado(EstadoVenta.BORRADOR);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setNumeroFactura(generarNumeroFactura(sucursal.getId()));
        venta.setCajeroUsuario((User) connectedUser.getPrincipal());

        DetalleVenta det = new DetalleVenta();
        det.setTipoItem(TipoItemVenta.SERVICIO);
        det.setReferenciaId(servicio.getId());
        det.setDescripcionSnapshot(ns(servicio.getNombre()) + " - " + ns(servicio.getDescripcion()));
        det.setPrecioUnitarioSnapshot(servicio.getPrecio());
        det.setCantidad(cantidad);
        det.setDescuento(BigDecimal.ZERO);
        det.setImpuesto(BigDecimal.ZERO);

        BigDecimal totalLinea = servicio.getPrecio()
                .multiply(cantidad)
                .setScale(2, RoundingMode.HALF_UP);

        det.setTotalLinea(totalLinea);

        venta.agregarDetalle(det);

        venta.setSubtotal(totalLinea);
        venta.setDescuentoTotal(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        venta.setImpuestoTotal(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        venta.setTotal(totalLinea);

        Venta saved = ventaRepository.save(venta);
        return ventaMapper.toResponse(saved);
    }

    @Transactional
    public VentaResponse crearVentaProductos(CrearVentaProductoRequest request, Authentication connectedUser) {

        if (request == null) {
            throw new BussinessException(BusinessErrorCodes.VALIDATION_ERROR);
        }
        if (request.getSucursalId() == null) {
            throw new BussinessException(BusinessErrorCodes.VENTA_SUCURSAL_REQUIRED);
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BussinessException(BusinessErrorCodes.VENTA_ITEMS_REQUIRED);
        }
        if (connectedUser == null || connectedUser.getPrincipal() == null) {
            throw new BussinessException(BusinessErrorCodes.BAD_CREDENTIALS);
        }

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.VENTA_SUCURSAL_NOT_FOUND));

        Cliente cliente = null;
        if (request.getClienteId() != null) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new BussinessException(BusinessErrorCodes.VENTA_CLIENTE_NOT_FOUND));
        }

        Venta venta = new Venta();
        venta.setSucursal(sucursal);
        venta.setCliente(cliente); // puede ser null (mostrador)
        venta.setEstado(EstadoVenta.BORRADOR);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setCajeroUsuario((User) connectedUser.getPrincipal());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemProductoRequest item : request.getItems()) {

            if (item == null) {
                throw new BussinessException(BusinessErrorCodes.VENTA_ITEM_INVALIDO);
            }
            if (item.getProductoId() == null) {
                throw new BussinessException(BusinessErrorCodes.VENTA_PRODUCTO_REQUIRED);
            }
            if (item.getCantidad() == null || item.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BussinessException(BusinessErrorCodes.VENTA_CANTIDAD_INVALIDA);
            }

            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new BussinessException(BusinessErrorCodes.VENTA_PRODUCTO_NOT_FOUND));

            if (producto.getPrecioVenta() == null || producto.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BussinessException(BusinessErrorCodes.VENTA_PRODUCTO_PRECIO_INVALIDO);
            }

            DetalleVenta det = new DetalleVenta();
            det.setTipoItem(TipoItemVenta.PRODUCTO);
            det.setReferenciaId(producto.getId());
            det.setDescripcionSnapshot(ns(producto.getNombre()));
            det.setPrecioUnitarioSnapshot(producto.getPrecioVenta());
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

        // Generar numeroFactura atomico con bloqueo
        venta.setNumeroFactura(generarNumeroFactura(sucursal.getId()));

        Venta saved = ventaRepository.saveAndFlush(venta);
        return ventaMapper.toResponse(saved);
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public String generarNumeroFactura(Long sucursalId) {
        // Obtenemos la sucursal con bloqueo pesimista
        Sucursal sucursal = sucursalRepository.findByIdWithLock(sucursalId)
                .orElseThrow(() -> new BussinessException(BusinessErrorCodes.VENTA_SUCURSAL_NOT_FOUND));

        Long siguiente = sucursal.getUltimoNumeroFactura() + 1;
        sucursal.setUltimoNumeroFactura(siguiente);
        sucursalRepository.save(sucursal);

        String prefijo = "S" + sucursal.getId();
        return prefijo + "-" + String.format("%08d", siguiente);
    }

    private static String ns(String s) {
        return s == null ? "" : s;
    }

    public VentaResponse findById(Long id) {
        return ventaRepository.findById(id)
                .map(ventaMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
    }

}