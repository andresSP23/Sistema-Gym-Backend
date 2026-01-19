package com.ansicode.SistemaAdministracionGym.dashboard;

import com.ansicode.SistemaAdministracionGym.asistencia.AsistenciaRepository;
import com.ansicode.SistemaAdministracionGym.categoriaproducto.CategoriaProductoRepository;
import com.ansicode.SistemaAdministracionGym.cliente.ClienteRepository;
import com.ansicode.SistemaAdministracionGym.movimientoinventario.MovimientoInventarioRepository;
import com.ansicode.SistemaAdministracionGym.producto.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

//    private final ClienteRepository clienteRepository;
//    private final ProductoRepository productoRepository;
//    private final CategoriaProductoRepository categoriaProductoRepository;
//    private final AsistenciaRepository asistenciaRepository;
//    private final MovimientoInventarioRepository movimientoInventarioRepository;
//    private final DashboardRepository dashboardRepository;
//
//
//    public DashBoardResumenResponse obtenerResumen(
//            LocalDate fechaInicio,
//            LocalDate fechaFin
//    ) {
//
//        // Normalización de fechas
//        LocalDate inicio = fechaInicio != null ? fechaInicio : LocalDate.now();
//        LocalDate fin = fechaFin != null ? fechaFin : inicio;
//
//        if (fin.isBefore(inicio)) {
//            throw new IllegalArgumentException(
//                    "La fecha fin no puede ser menor a la fecha inicio"
//            );
//        }
//
//        LocalDateTime inicioDateTime = inicio.atStartOfDay();
//        LocalDateTime finDateTime = fin.atTime(23, 59, 59);
//
//        DashBoardResumenResponse response = new DashBoardResumenResponse();
//
//        // Clientes
//        response.setTotalClientes(clienteRepository.count());
//        response.setClientesEnRango(
//                dashboardRepository.clientesEnRango(inicio, fin)
//        );
//
//        // Asistencias
//        response.setAsistenciasEnRango(
//                dashboardRepository.asistenciasEnRango(
//                        inicioDateTime,
//                        finDateTime
//                )
//        );
//
//        dashboardRepository
//                .ultimaAsistencia(PageRequest.of(0, 1))
//                .stream()
//                .findFirst()
//                .ifPresent(a -> response.setUltimaAsistencia(
//                        new AsistenciaUltimaDto(
//                                a.getCliente().getNombres(),
//                                a.getFechaEntrada()
//                        )
//                ));
//
//        // Productos
//        response.setTotalProductos(productoRepository.count());
//        response.setTotalCategorias(categoriaProductoRepository.count());
//        response.setProductosStockBajo(
//                productoRepository.countProductosConStockBajo(5)
//
//        );
//
//        // Ventas
//        response.setTotalVentasEnRango(
//                dashboardRepository.totalVentas(
//                        inicioDateTime,
//                        finDateTime
//                )
//        );
//
//        response.setGananciaVentasEnRango(
//                dashboardRepository.gananciaVentas(
//                        inicioDateTime,
//                        finDateTime
//                )
//        );
//
//        // Membresías
//        response.setGananciaMembresiasEnRango(
//                dashboardRepository.gananciaMembresias(
//                        inicioDateTime,
//                        finDateTime
//                )
//        );
//
//        response.setPagosMembresiaEnRango(
//                dashboardRepository.pagosMembresiaEnRango(
//                        inicioDateTime,
//                        finDateTime
//                )
//        );
//
//        // Inventario
//        response.setMovimientosInventarioEnRango(
//                dashboardRepository.movimientosInventario(
//                        inicioDateTime,
//                        finDateTime
//                )
//        );
//
//        return response;
//    }
}
