package com.ansicode.SistemaAdministracionGym.clase;

import com.ansicode.SistemaAdministracionGym.user.User;
import org.springframework.stereotype.Service;

@Service
public class ClaseMapper {

    // Request → Entidad
    public Clase toClase(ClaseRequest request, User entrenador) {
        return Clase.builder()
                .nombre(request.getNombre())
                .entrenador(entrenador)
                .build();
    }

    // Entidad → Response
    public ClaseResponse toClaseResponse(Clase clase) {
        return ClaseResponse.builder()
                .id(clase.getId()) // ✅ ID de la entidad (Long)
                .nombre(clase.getNombre())
                .entrenadorId(clase.getEntrenador().getId())
                .entrenadorNombre(clase.getEntrenador().getNombre() + " " + clase.getEntrenador().getApellido())
                .build();
    }
}
