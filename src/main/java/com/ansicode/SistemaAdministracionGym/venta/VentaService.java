package com.ansicode.SistemaAdministracionGym.venta;

import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteRepository;
import com.ansicode.SistemaAdministracionGym.common.PageResponse;
import com.ansicode.SistemaAdministracionGym.comprobanteventa.ComprobanteVentaResponse;
import com.ansicode.SistemaAdministracionGym.comprobanteventa.ComprobanteVentaService;
import com.ansicode.SistemaAdministracionGym.enums.EstadoVenta;
import com.ansicode.SistemaAdministracionGym.movimientoinventario.MovimientoInventarioService;
import com.ansicode.SistemaAdministracionGym.producto.Producto;
import com.ansicode.SistemaAdministracionGym.producto.ProductoRepository;
import com.ansicode.SistemaAdministracionGym.user.User;
import com.ansicode.SistemaAdministracionGym.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {



    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    private final VentaMapper ventaMapper;
    private final DetalleVentaMapper detalleVentaMapper;
    private final MovimientoInventarioService movimientoInventarioService;
    private final ComprobanteVentaService  comprobanteVentaService;

    public VentaResponse create(VentaRequest request, Authentication connectedUser) {

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        User vendedor = (User) connectedUser.getPrincipal();

        // Crear venta con total inicial
        Venta venta = ventaMapper.toVenta(cliente, vendedor, request.getFechaVenta(), request.getMetodoPago());
        venta.setTotal(BigDecimal.ZERO);
        venta.setActivo(true);
        venta = ventaRepository.save(venta);

        BigDecimal total = BigDecimal.ZERO;
        List<DetalleVentaResponse> detalleResponses = new ArrayList<>();

        // Procesar cada producto
        for (DetalleVentaItemRequest item : request.getItems()) {

            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

            if (producto.getStock() < item.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para " + producto.getNombre());
            }

            // Cálculo de subtotal y suma al total
            BigDecimal subtotal = producto.getPrecioVenta()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);

            // Registrar movimiento y descontar stock
            movimientoInventarioService.registrarSalida(producto, item.getCantidad(), vendedor);

            // Guardar detalle
            DetalleVenta detalle = detalleVentaMapper.toDetalleVenta(
                    venta,
                    producto,
                    item.getCantidad(),
                    producto.getPrecioVenta(),
                    subtotal
            );
            detalleVentaRepository.save(detalle);
            detalleResponses.add(detalleVentaMapper.toDetalleVentaResponse(detalle));
        }

        // Actualizar total real de la venta
        venta.setTotal(total);
        ventaRepository.save(venta);

        // === GENERAR COMPROBANTE AUTOMÁTICAMENTE ===
        try {
            // Pasamos venta y lista de detalles ya procesada
            ComprobanteVentaResponse comprobanteResponse =
                    comprobanteVentaService.generarYGuardarComprobante(venta.getId() , detalleResponses);

            // Opcional: devolver PDF en Base64 junto con la venta
            // byte[] pdfBytes = comprobanteVentaService.generarPdfComprobante(comprobanteResponse.getId());
            // ventaResponse.setPdfBase64(Base64.getEncoder().encodeToString(pdfBytes));

        } catch (Exception e) {
            // No bloquear la venta si falla el comprobante
            System.err.println("Error al generar comprobante: " + e.getMessage());
        }

        // Devolver venta con detalles
        return ventaMapper.toVentaResponse(venta, detalleResponses);
    }


}
