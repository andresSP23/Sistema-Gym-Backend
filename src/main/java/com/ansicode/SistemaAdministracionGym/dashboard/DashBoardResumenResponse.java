package com.ansicode.SistemaAdministracionGym.dashboard;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DashBoardResumenResponse {

    // Clientes
    private long totalClientes;
    private long clientesEnRango;

    // Asistencias
    private long asistenciasEnRango;
    private AsistenciaUltimaDto ultimaAsistencia;

    // Productos
    private long totalProductos;
    private long totalCategorias;
    private long productosStockBajo;

    // Ventas
    private long totalVentasEnRango;
    private BigDecimal gananciaVentasEnRango;

    // Membresías
    private BigDecimal gananciaMembresiasEnRango;
    private List<MembresiaPagoDto> pagosMembresiaEnRango;

    // Inventario
    private long movimientosInventarioEnRango;
}
