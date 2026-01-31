package com.ansicode.SistemaAdministracionGym.pago;

import com.ansicode.SistemaAdministracionGym.categoriaproducto.CategoriaProducto;
import com.ansicode.SistemaAdministracionGym.categoriaproducto.CategoriaProductoRepository;
import com.ansicode.SistemaAdministracionGym.cliente.Cliente;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteRepository;
import com.ansicode.SistemaAdministracionGym.clientesuscripcion.ClienteSuscripcionRepository;
import com.ansicode.SistemaAdministracionGym.common.AuditedEntity;
import com.ansicode.SistemaAdministracionGym.detalleventa.DetalleVenta;
import com.ansicode.SistemaAdministracionGym.enums.EstadoVenta;
import com.ansicode.SistemaAdministracionGym.enums.MetodoPago;
import com.ansicode.SistemaAdministracionGym.enums.TipoComprobante;
import com.ansicode.SistemaAdministracionGym.enums.TipoItemVenta;
import com.ansicode.SistemaAdministracionGym.movimientoinventario.MovimientoInventarioRepository;
import com.ansicode.SistemaAdministracionGym.producto.Producto;
import com.ansicode.SistemaAdministracionGym.producto.ProductoRepository;
import com.ansicode.SistemaAdministracionGym.servicio.Servicios;
import com.ansicode.SistemaAdministracionGym.servicio.ServiciosRepository;
import com.ansicode.SistemaAdministracionGym.sucursal.Sucursal;
import com.ansicode.SistemaAdministracionGym.sucursal.SucursalRepository;
import com.ansicode.SistemaAdministracionGym.user.User;
import com.ansicode.SistemaAdministracionGym.user.UserRepository;
import com.ansicode.SistemaAdministracionGym.venta.Venta;
import com.ansicode.SistemaAdministracionGym.venta.VentaRepository;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCaja;
import com.ansicode.SistemaAdministracionGym.sesioncaja.SesionCajaRepository;
import com.ansicode.SistemaAdministracionGym.enums.EstadoSesionCaja;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PagoServiceIntegrationTest {

    @Autowired
    private PagoService pagoService;
    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ServiciosRepository serviciosRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private SucursalRepository sucursalRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MovimientoInventarioRepository movimientoInventarioRepository;
    @Autowired
    private ClienteSuscripcionRepository clienteSuscripcionRepository;
    @Autowired
    private CategoriaProductoRepository categoriaProductoRepository;
    @Autowired
    private SesionCajaRepository sesionCajaRepository;

    private User adminUser;
    private Sucursal sucursal;
    private Cliente cliente;
    private CategoriaProducto categoria;

    @BeforeEach
    void setup() {
        // Setup base data
        adminUser = userRepository.save(User.builder()
                .nombre("Admin")
                .email("admin@test.com")
                .password("123456")
                .activa(true) // Fixed: enabled -> activa
                .build());

        // Mock Authentication
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(adminUser, null, List.of()));

        sucursal = sucursalRepository.save(Sucursal.builder()
                .nombre("Sucursal Test")
                .codigoSucursal("SUC-001") // Fixed: constraint violation
                .horaApertura(LocalTime.of(8, 0))
                .horaCierre(LocalTime.of(22, 0))
                .build());

        cliente = clienteRepository.save(Cliente.builder()
                .nombres("Juan")
                .apellidos("Perez")
                .cedula("1234567890")
                .email("juan@test.com")
                .telefono("0999999999")
                .direccion("Av. Test 123")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .isVisible(true)
                .build());

        categoria = categoriaProductoRepository.save(CategoriaProducto.builder()
                .nombre("General")
                .build());

        sesionCajaRepository.save(SesionCaja.builder()
                .sucursalId(sucursal.getId())
                .usuarioAperturaId(adminUser.getId())
                .fechaApertura(LocalDateTime.now())
                .estado(EstadoSesionCaja.ABIERTA)
                .baseInicialEfectivo(BigDecimal.ZERO)
                .build());
    }

    @Test
    void registrarPago_Producto_InventoryUpdate() {
        // 1. Create Product
        Producto producto = productoRepository.save(Producto.builder()
                .nombre("Protein Shake")
                .precioCompra(BigDecimal.valueOf(2.0))
                .precioVenta(BigDecimal.valueOf(5.0))
                .stock(10)
                .categoriaProducto(categoria)
                .build());

        // 2. Create Venta
        Venta venta = new Venta();
        venta.setSucursal(sucursal);
        venta.setCliente(cliente);
        venta.setEstado(EstadoVenta.BORRADOR); // Fixed: PENDIENTE -> BORRADOR
        venta.setTotal(BigDecimal.valueOf(10.0)); // 2 units * 5.0
        venta.setCajeroUsuario(adminUser); // Fixed: setUser -> setCajeroUsuario
        venta.setNumeroFactura("FACT-001");

        DetalleVenta detalle = DetalleVenta.builder()
                .venta(venta)
                .descripcionSnapshot(producto.getNombre())
                .referenciaId(producto.getId())
                .tipoItem(TipoItemVenta.PRODUCTO)
                .cantidad(BigDecimal.valueOf(2))
                .precioUnitarioSnapshot(BigDecimal.valueOf(5.0))
                .totalLinea(BigDecimal.valueOf(10.0))
                .descuento(BigDecimal.ZERO) // Added mandatory field
                .impuesto(BigDecimal.ZERO) // Added mandatory field
                .build();

        venta.setDetalles(new java.util.ArrayList<>(List.of(detalle)));
        ventaRepository.save(venta);

        // 3. Prepare Request
        PagoRequest request = new PagoRequest();
        request.setVentaId(venta.getId());
        request.setMetodo(MetodoPago.EFECTIVO);
        request.setMonto(BigDecimal.valueOf(10.0));
        request.setEfectivoRecibido(BigDecimal.valueOf(20.0));
        request.setTipoComprobante(TipoComprobante.FACTURA);
        request.setClienteId(cliente.getId());
        // Added explicit tipoOperacion even if Service deduces it, to satisfy
        // validation if any
        request.setTipoOperacion(com.ansicode.SistemaAdministracionGym.enums.TipoOperacionPago.PRODUCTO);

        // 4. Execute Service
        PagoResponse response = pagoService.registrarPago(request,
                SecurityContextHolder.getContext().getAuthentication());

        // 5. Assertions
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(10.00).setScale(2), response.getMonto());

        // Verify Inventory (Logic via Listener)
        // Since test is @Transactional, changes should be visible in same tx context if
        // flushed?
        // Wait, Listener runs in the same thread/tx usually unless @Async.
        // My implementation of Listener is sync.

        // Reload product
        Producto updatedProduct = productoRepository.findById(producto.getId()).orElseThrow();
        assertEquals(8, updatedProduct.getStock(), "Stock should be reduced by 2");

        // Verify Movement Record
        var movimientos = movimientoInventarioRepository.findAll();
        assertFalse(movimientos.isEmpty());
        // Simple check to see if any movement relates to our product
        boolean movementExists = movimientos.stream()
                .anyMatch(m -> m.getProducto().getId().equals(producto.getId()));
        assertTrue(movementExists, "Movement should exist for product");
    }

    @Test
    void registrarPago_Servicio_SuscripcionCreated() {
        // 1. Create Service (Membership)
        Servicios servicio = serviciosRepository.save(Servicios.builder()
                .nombre("Membresia Mensual")
                .precio(BigDecimal.valueOf(30.0))
                .esSuscripcion(true)
                .duracionDias(30)
                .build());

        // 2. Create Venta
        Venta venta = new Venta();
        venta.setSucursal(sucursal);
        venta.setCliente(cliente);
        venta.setEstado(EstadoVenta.BORRADOR);
        venta.setTotal(BigDecimal.valueOf(30.0));
        venta.setCajeroUsuario(adminUser); // Fixed: setUser -> setCajeroUsuario
        venta.setNumeroFactura("FACT-002");

        DetalleVenta detalle = DetalleVenta.builder()
                .venta(venta)
                .descripcionSnapshot(servicio.getNombre())
                .referenciaId(servicio.getId())
                .tipoItem(TipoItemVenta.SERVICIO)
                .cantidad(BigDecimal.valueOf(1))
                .precioUnitarioSnapshot(BigDecimal.valueOf(30.0))
                .totalLinea(BigDecimal.valueOf(30.0))
                .descuento(BigDecimal.ZERO) // Added mandatory field
                .impuesto(BigDecimal.ZERO) // Added mandatory field
                .build();

        venta.setDetalles(new java.util.ArrayList<>(List.of(detalle)));
        ventaRepository.save(venta);

        // 3. Prepare Request
        PagoRequest request = new PagoRequest();
        request.setVentaId(venta.getId());
        request.setMetodo(MetodoPago.EFECTIVO);
        request.setMonto(BigDecimal.valueOf(30.0));
        request.setEfectivoRecibido(BigDecimal.valueOf(30.0));
        request.setTipoComprobante(TipoComprobante.RECIBO);
        request.setClienteId(cliente.getId());
        request.setTipoOperacion(com.ansicode.SistemaAdministracionGym.enums.TipoOperacionPago.SERVICIO);

        // 4. Execute Service
        PagoResponse response = pagoService.registrarPago(request,
                SecurityContextHolder.getContext().getAuthentication());

        // 5. Assertions
        assertNotNull(response);

        // Verify Subscription (Logic via Listener)
        var suscripciones = clienteSuscripcionRepository.findAll();
        assertFalse(suscripciones.isEmpty(), "Subscription should be created");

        boolean exists = suscripciones.stream()
                .anyMatch(s -> s.getCliente().getId().equals(cliente.getId()) &&
                        s.getServicio().getId().equals(servicio.getId()));
        assertTrue(exists, "Specific subscription should exist");
    }
}
