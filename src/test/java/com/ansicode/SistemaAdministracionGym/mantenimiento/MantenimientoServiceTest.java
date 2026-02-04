package com.ansicode.SistemaAdministracionGym.mantenimiento;

import com.ansicode.SistemaAdministracionGym.enums.TipoMantenimiento;
import com.ansicode.SistemaAdministracionGym.equipamiento.Equipamiento;
import com.ansicode.SistemaAdministracionGym.equipamiento.EquipamientoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MantenimientoServiceTest {

    @Mock
    private MantenimientoRepository repository;
    @Mock
    private EquipamientoRepository equipamientoRepository;
    @Mock
    private MantenimientoMapper mapper;

    @InjectMocks
    private MantenimientoService service;

    @Test
    void create_PreventiveWithFrequency_ShouldScheduleNextMaintenance() {
        // Arrange
        Long equipId = 1L;
        Equipamiento equip = Equipamiento.builder()
                .id(equipId)
                .nombre("Cinta Correr")
                .frecuenciaMantenimientoDias(30)
                .build();

        MantenimientoRequest request = new MantenimientoRequest();
        request.setEquipamientoId(equipId);
        request.setTipo(TipoMantenimiento.PREVENTIVO);
        request.setFechaRealizacion(LocalDateTime.now());

        Mantenimiento mantenimientoEntity = new Mantenimiento();
        mantenimientoEntity.setEquipamiento(equip);
        mantenimientoEntity.setTipo(TipoMantenimiento.PREVENTIVO);

        when(equipamientoRepository.findById(equipId)).thenReturn(Optional.of(equip));
        when(mapper.toEntity(request, equip)).thenReturn(mantenimientoEntity);
        when(repository.save(any(Mantenimiento.class))).thenReturn(mantenimientoEntity);
        when(mapper.toResponse(any(Mantenimiento.class))).thenReturn(new MantenimientoResponse());

        // Act
        service.create(request, Mockito.mock(org.springframework.security.core.Authentication.class));

        // Assert
        // Verify Next Maintenance is set in created record
        Assertions.assertNotNull(mantenimientoEntity.getProximoMantenimientoSugerido());

        // Verify Equipamiento was updated
        verify(equipamientoRepository).save(equip);
        Assertions.assertNotNull(equip.getProximoMantenimiento());
    }

    @Test
    void create_Corrective_ShouldNotScheduleNextMaintenance() {
        // Arrange
        Long equipId = 2L;
        Equipamiento equip = Equipamiento.builder()
                .id(equipId)
                .nombre("Mancuerna")
                .frecuenciaMantenimientoDias(30)
                .build();

        MantenimientoRequest request = new MantenimientoRequest();
        request.setEquipamientoId(equipId);
        request.setTipo(TipoMantenimiento.CORRECTIVO);
        request.setFechaRealizacion(LocalDateTime.now());

        Mantenimiento mantenimientoEntity = new Mantenimiento();
        mantenimientoEntity.setEquipamiento(equip);
        mantenimientoEntity.setTipo(TipoMantenimiento.CORRECTIVO);

        when(equipamientoRepository.findById(equipId)).thenReturn(Optional.of(equip));
        when(mapper.toEntity(request, equip)).thenReturn(mantenimientoEntity);
        when(repository.save(any(Mantenimiento.class))).thenReturn(mantenimientoEntity);
        when(mapper.toResponse(any(Mantenimiento.class))).thenReturn(new MantenimientoResponse());

        // Act
        service.create(request, Mockito.mock(org.springframework.security.core.Authentication.class));

        // Assert
        Assertions.assertNull(mantenimientoEntity.getProximoMantenimientoSugerido());
        // Should NOT save equipment update
        verify(equipamientoRepository, Mockito.never()).save(equip);
    }
}
