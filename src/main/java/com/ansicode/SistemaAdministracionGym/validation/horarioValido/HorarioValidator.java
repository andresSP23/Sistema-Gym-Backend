package com.ansicode.SistemaAdministracionGym.validation.horarioValido;

import com.ansicode.SistemaAdministracionGym.sucursal.SucursalRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HorarioValidator  implements ConstraintValidator<HorarioValido, SucursalRequest> {
    @Override
    public void initialize(HorarioValido constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(SucursalRequest value, ConstraintValidatorContext context) {
        if (value.getHoraApertura() == null || value.getHoraCierre() == null) {
            return true;
        }
        //la validacion en si
        return value.getHoraCierre().isAfter(value.getHoraApertura());
    }


}
