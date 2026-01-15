package com.ansicode.SistemaAdministracionGym.validation.horarioValido;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HorarioValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface HorarioValido {

    String message() default "La hora de cierre debe ser posterior a la hora de apertura";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
